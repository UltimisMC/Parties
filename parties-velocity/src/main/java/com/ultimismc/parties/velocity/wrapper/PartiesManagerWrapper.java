package com.ultimismc.parties.velocity.wrapper;

import com.google.gson.Gson;
import com.ultimismc.parties.api.party.PartiesManager;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import redis.clients.jedis.Jedis;

import java.util.*;

public class PartiesManagerWrapper implements PartiesManager {

    private final List<Party> parties = new ArrayList<>();

    @Override
    public Party getParty(PartyPlayer partyPlayer) {
        return parties.stream().filter(party -> party.getOwner().equals(partyPlayer) || party.isMember(partyPlayer.getUuid()))
                .findFirst().orElse(null);
    }

    @Override
    public Party getParty(UUID uuid) {
        return parties.stream().filter(party -> party.getOwner().getUuid().equals(uuid) || party.isMember(uuid))
                .findFirst().orElse(null);
    }

    @Override
    public Party createFor(PartyPlayer partyPlayer) {
        PartyWrapper party = new PartyWrapper(partyPlayer);
        partyPlayer.setRole(PartyRole.LEADER);
        party.addMember(partyPlayer);
        parties.add(party);
        try (Jedis jedis = PartiesVelocity.getInstance().getRedis().getResource()) {
            jedis.set("parties:" + partyPlayer.getUuid(), new Gson().toJson(party.toJson()));
            jedis.publish("core:parties", new Gson().toJson(party.toJson()));
        }
        return party;
    }

    @Override
    public boolean hasParty(PartyPlayer partyPlayer) {
        return false;
    }

    @Override
    public void removeParty(Party party) {
        parties.remove(party);
    }
}
