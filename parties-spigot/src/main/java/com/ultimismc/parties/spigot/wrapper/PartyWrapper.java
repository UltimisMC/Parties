package com.ultimismc.parties.spigot.wrapper;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.player.PartyPlayer;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class PartyWrapper implements Party {

    private final PartyPlayer owner;
    private boolean open = false, privateGame = false;
    private final List<PartyPlayer> members = new ArrayList<>();


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
        return members.add(partyPlayer);
    }

    @Override
    public boolean removeMember(PartyPlayer partyPlayer) {
        if (!isMember(partyPlayer.getUuid()))
            return false;
        return members.remove(partyPlayer);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }

    @Override
    public boolean isPrivateGame() {
        return privateGame;
    }

    @Override
    public void setPrivateGame(boolean privateGame) {
        this.privateGame = privateGame;
    }

    @Override
    public boolean isMember(UUID uuid) {
        return members.stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(uuid));
    }

    @Override
    public void disband() {
        getMembers().clear();
        ApiProvider.getApi().getPartiesManager().removeParty(this);
    }
}
