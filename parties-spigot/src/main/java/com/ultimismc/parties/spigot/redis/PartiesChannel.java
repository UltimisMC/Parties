package com.ultimismc.parties.spigot.redis;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.common.connection.redis.RedisChannel;
import com.ultimismc.parties.spigot.wrapper.PartyPlayerWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartiesChannel extends RedisChannel {

    private final Gson gson = new Gson();
    private final PartiesAPI api = ApiProvider.getApi();

    public PartiesChannel() {
        super("core:parties");
    }

    @Override
    public void handle(String message) {
        JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
        if (jsonObject.get("action") != null) {
            String action = jsonObject.get("action").getAsString();
            if (action.equalsIgnoreCase("disband")) {
                UUID uuid = UUID.fromString(jsonObject.get("name").getAsString().split(";")[0]);
                String name = jsonObject.get("name").getAsString().split(";")[1];
                PartyPlayer partyOwner = new PartyPlayerWrapper(uuid, name);
                Party party = api.getPartiesManager().getParty(partyOwner);
                if (party != null)
                    party.disband();
                return;
            }
        }
        String[] ownerSplit = jsonObject.get("owner").getAsString().split(";");
        String ownerUuid = ownerSplit[0];
        String ownerName = ownerSplit[1];
        boolean privateGames = jsonObject.get("privateGames").getAsBoolean();
        boolean open = jsonObject.get("openParty").getAsBoolean();
        List<String> members = gson.fromJson(jsonObject.get("members").getAsString(), ArrayList.class);
        PartyPlayer partyOwner = new PartyPlayerWrapper(UUID.fromString(ownerUuid), ownerName);
        Party party = api.getPartiesManager().getParty(partyOwner.getUuid());
        if (party == null) {
            party = api.getPartiesManager().createFor(partyOwner);
            for (String s : members) {
                String[] split = s.split(";");
                UUID uuid = UUID.fromString(split[0]);
                String name = split[1];
                PartyRole role = PartyRole.valueOf(split[2]);
                PartyPlayer partyPlayer = new PartyPlayerWrapper(uuid, name);
                partyPlayer.setRole(role);
                party.addMember(partyPlayer);
            }
        }
        party.setPrivateGame(privateGames);
        party.setOpen(open);
        List<PartyPlayer> parsedMembers = new ArrayList<>();
        for (String s : members) {
            String[] split = s.split(";");
            UUID uuid = UUID.fromString(split[0]);
            String name = split[1];
            PartyRole role = PartyRole.valueOf(split[2]);
            PartyPlayer partyPlayer = new PartyPlayerWrapper(uuid, name);
            partyPlayer.setRole(role);
            parsedMembers.add(partyPlayer);
        }
        List<PartyPlayer> toAdd = new ArrayList<>(), toRemove = new ArrayList<>();
        for (PartyPlayer partyPlayer : parsedMembers) {
            if (!party.isMember(partyPlayer.getUuid())) {
                toAdd.add(partyPlayer);
            }
        }
        for (PartyPlayer partyPlayer : party.getMembers()) {
            if (!parsedMembers.contains(partyPlayer)) {
                toRemove.add(partyPlayer);
            }
        }
        for (PartyPlayer partyPlayer : toAdd) {
            party.addMember(partyPlayer);
        }
        for (PartyPlayer partyPlayer : toRemove) {
            party.removeMember(partyPlayer);
        }
    }

}
