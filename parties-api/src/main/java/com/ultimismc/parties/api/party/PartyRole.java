package com.ultimismc.parties.api.party;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PartyRole {

    MEMBER(1),
    MODERATOR(2),
    LEADER(3);

    @Getter
    private final int permissionLevel;

    public PartyRole getNext() {
        return ordinal() == values().length - 1 ? LEADER : values()[ordinal()+1];
    }

    public PartyRole getPrevious() {
        return ordinal() == 0 ? MEMBER : values()[ordinal()-1];
    }

}
