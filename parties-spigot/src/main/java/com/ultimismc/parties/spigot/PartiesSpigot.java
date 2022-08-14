package com.ultimismc.parties.spigot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.common.connection.Connection;
import com.ultimismc.parties.common.connection.ConnectionResult;
import com.ultimismc.parties.common.connection.Credentials;
import com.ultimismc.parties.common.connection.redis.RedisConnection;
import com.ultimismc.parties.common.platform.PlatformPlugin;
import com.ultimismc.parties.spigot.redis.PartiesChannel;
import com.ultimismc.parties.spigot.wrapper.PartiesApiWrapper;
import com.ultimismc.parties.spigot.wrapper.PartyInit;
import com.ultimismc.parties.spigot.wrapper.PartyPlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.UUID;

public class PartiesSpigot extends JavaPlugin implements PlatformPlugin<JavaPlugin> {

    private RedisConnection redisConnection;

    @Override
    public void onEnable() {
        ApiProvider.provide(new PartiesApiWrapper());
        saveDefaultConfig();
        Credentials credentials = Credentials.builder()
                .host(getConfig().getString("redis.host"))
                .port(getConfig().getInt("redis.port"))
                .password(getConfig().getString("redis.password"))
                .build();
        this.redisConnection = new RedisConnection(credentials);
        ConnectionResult result = this.redisConnection.open();
        if (result == ConnectionResult.FAILURE) {
            getLogger().severe("Failed to connect to redis server!");
            setEnabled(false);
            return;
        }
        initParties();
        redisConnection.subscribe(new PartiesChannel());
        if (Bukkit.getPluginManager().isPluginEnabled("BedWars1058")) {
            new PartyInit();
        }
    }

    @Override
    public Connection<JedisPool> getRedis() {
        return redisConnection;
    }

    private void initParties() {
        Gson gson = new Gson();
        try (Jedis jedis = getRedis().getResource()) {
            for (String k : jedis.keys("parties:*")) {
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

}
