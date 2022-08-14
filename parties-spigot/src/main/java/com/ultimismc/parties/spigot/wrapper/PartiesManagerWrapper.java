package com.ultimismc.parties.spigot.wrapper;

import com.ultimismc.parties.api.party.PartiesManager;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import lombok.Getter;

import java.util.*;

public class PartiesManagerWrapper implements PartiesManager {

    @Getter private final List<Party> parties = new ArrayList<>();

    @Override
    public Party getParty(PartyPlayer partyPlayer) {
        return parties.stream().filter(party -> party.getOwner().equals(partyPlayer)
                        || party.isMember(partyPlayer.getUuid()))
                .findFirst().orElse(null);
    }

    @Override
    public Party getParty(UUID uuid) {
        for (Party party : parties) {
            if (party.isMember(uuid))
                return party;
        }
        return null;
    }

    @Override
    public Party createFor(PartyPlayer partyPlayer) {
        Party party = new PartyWrapper(partyPlayer);
        partyPlayer.setRole(PartyRole.LEADER);
        party.addMember(partyPlayer);
        parties.add(party);
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
