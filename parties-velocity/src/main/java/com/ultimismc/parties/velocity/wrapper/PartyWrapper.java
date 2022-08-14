package com.ultimismc.parties.velocity.wrapper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import redis.clients.jedis.Jedis;

import java.util.*;

@RequiredArgsConstructor
public class PartyWrapper implements Party {

    private final PartyPlayer owner;
    private boolean open = false, privateGame = false;
    private final List<PartyPlayer> members = new ArrayList<>();
    @Getter private final List<UUID> invited = new ArrayList<>();
    @Getter private final List<UUID> banned = new ArrayList<>();
    @Getter @Setter private boolean muted = false;

    @Override
    public PartyPlayer getOwner() {
        return owner;
    }

    @Override
    public Collection<PartyPlayer> getMembers() {
        return members;
    }

    @Override
    public boolean addMember(PartyPlayer partyPlayer) {
        if (isMember(partyPlayer.getUuid()))
            return false;
        members.add(partyPlayer);
        JsonObject jsonObject = toJson();
        List<String> added = new ArrayList<>();
        added.add(partyPlayer.getUuid() + ";" + partyPlayer.getName() + ";" + partyPlayer.getRole());
        jsonObject.addProperty("added", new Gson().toJson(added));
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.set("parties:" + owner.getUuid(), new Gson().toJson(toJson()));
            jedis.publish("core:parties", new Gson().toJson(toJson()));
        }
        return true;
    }

    @Override
    public boolean removeMember(PartyPlayer partyPlayer) {
        if (!isMember(partyPlayer.getUuid()))
            return false;
        members.remove(partyPlayer);
        JsonObject jsonObject = toJson();
        List<String> removed = new ArrayList<>();
        removed.add(partyPlayer.getUuid() + ";" + partyPlayer.getName() + ";" + partyPlayer.getRole());
        jsonObject.addProperty("removed", new Gson().toJson(removed));
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.set("parties:" + owner.getUuid(), new Gson().toJson(toJson()));
            jedis.publish("core:parties", new Gson().toJson(toJson()));
        }
        return true;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.set("parties:" + owner.getUuid(), new Gson().toJson(toJson()));
            jedis.publish("core:parties", new Gson().toJson(toJson()));
        }
    }

    @Override
    public boolean isPrivateGame() {
        return privateGame;
    }

    @Override
    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.set("parties:" + owner.getUuid(), new Gson().toJson(toJson()));
            jedis.publish("core:parties", new Gson().toJson(toJson()));
        }
    }

    @Override
    public void disband() {
        getMembers().clear();
        ApiProvider.getApi().getPartiesManager().removeParty(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "disband");
        jsonObject.addProperty("name", owner.getUuid() + ";" + owner.getName());
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.del("parties:" + owner.getUuid());
            jedis.publish("core:parties", new Gson().toJson(jsonObject));
        }
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("owner", owner.getUuid() + ";" + owner.getName());
        jsonObject.addProperty("privateGames", privateGame);
        jsonObject.addProperty("openParty", open);
        List<String> members = new ArrayList<>();
        for (PartyPlayer player : this.members) {
            members.add(player.getUuid() + ";" + player.getName() + ";" + player.getRole());
        }
        jsonObject.addProperty("members", new Gson().toJson(members));
        return jsonObject;
    }
}
