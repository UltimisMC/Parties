package com.ultimismc.parties.spigot.wrapper;

import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
public class PartyPlayerWrapper implements PartyPlayer {

    private final UUID uuid;
    private final String name;
    private PartyRole partyRole = PartyRole.MEMBER;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PartyRole getRole() {
        return partyRole;
    }

    @Override
    public void setRole(PartyRole role) {
        this.partyRole = role;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PartyPlayer))
            return false;
        PartyPlayer player = (PartyPlayer) object;
        return player.getUuid().equals(getUuid()) && player.getName().equals(getName());
    }

    @Override
    public int hashCode() {
        int code = 0;
        for (byte b : name.getBytes(StandardCharsets.UTF_8)) {
            code+=b;
        }
        for (byte b : uuid.toString().getBytes(StandardCharsets.UTF_8)) {
            code+=b;
        }
        return code;
    }
}
