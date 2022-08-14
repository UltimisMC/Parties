package com.ultimismc.parties.velocity;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.common.connection.Credentials;
import com.ultimismc.parties.common.connection.redis.RedisConnection;
import com.ultimismc.parties.common.platform.PlatformPlugin;
import com.ultimismc.parties.velocity.commands.PartyCommand;
import com.ultimismc.parties.velocity.listeners.PartyListener;
import com.ultimismc.parties.velocity.wrapper.PartiesApiWrapper;
import com.ultimismc.parties.velocity.wrapper.PartyPlayerWrapper;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;

@Plugin(
        id = "parties",
        name = "UltimisMC Parties",
        authors = "AkramL",
        version = "${parent.version}"
)
public class PartiesVelocity {

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    private RedisConnection redisConnection;
    boolean enable = true;
    @Getter private static PartiesVelocity instance;

    @Inject
    public PartiesVelocity(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        ApiProvider.provide(new PartiesApiWrapper());
        instance = this;
        copyConfigFile();
        StringBuilder builder = new StringBuilder();
        try (FileReader fileReader = new FileReader("plugins/parties/config.json")) {
            int i;
            while ((i = fileReader.read()) != -1) {
                builder.append((char) i);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            enable = false;
            return;
        }
        Gson gson = new Gson();
        JsonObject parent = gson.fromJson(builder.toString(), JsonObject.class);
        Credentials credentials = Credentials.builder()
                .host(parent.get("host").getAsString())
                .port(parent.get("port").getAsInt())
                .password(parent.get("password").getAsString())
                .build();
        this.redisConnection = new RedisConnection(credentials);
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        if (!enable)
            return;
        initParties();
        initCommands();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyConfigFile() {
        if (!new File("plugins/parties").exists())
            new File("plugins/parties").mkdir();
        File file = new File("plugins/parties/config.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
                try (InputStream stream = getClass().getResourceAsStream("/config.json")) {
                    if (stream == null)
                        return;
                    Files.copy(stream, file.toPath());
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public RedisConnection getRedis() {
        return redisConnection;
    }

    private void initParties() {
        Gson gson = new Gson();
        try (Jedis jedis = getRedis().getResource()) {
            for (String k : jedis.keys("party:*")) {
                String v = jedis.get(k);
                JsonObject jsonObject = gson.fromJson(v, JsonObject.class);
                String ownerPattern = jsonObject.get("owner").getAsString();
                String[] ownerSplit = ownerPattern.split(";");
                PartyPlayer partyOwner = new PartyPlayerWrapper(UUID.fromString(ownerSplit[0]), ownerSplit[1]);
                Party party = ApiProvider.getApi().getPartiesManager().createFor(partyOwner);
                for (String pattern : (ArrayList<String>) gson.fromJson(jsonObject.get("members").getAsString(), ArrayList.class)) {
                    String[] split = pattern.split(";");
                    PartyPlayer partyPlayer = new PartyPlayerWrapper(UUID.fromString(split[0]), split[1]);
                    if (partyPlayer.equals(partyOwner))
                        continue;
                    partyPlayer.setRole(PartyRole.valueOf(split[2]));
                    party.addMember(partyPlayer);
                }
            }
        }
    }

    private void initCommands() {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register(
                commandManager.metaBuilder("party").aliases("p").build(),
                new PartyCommand()
        );
        server.getEventManager().register(this, new PartyListener());
    }

}
