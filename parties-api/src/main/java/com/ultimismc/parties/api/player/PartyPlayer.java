package com.ultimismc.parties.api.player;

import com.ultimismc.parties.api.party.PartyRole;

import java.util.UUID;

/**
 * @author AkramL
 * @since 1.0-BETA
 */
public interface PartyPlayer {

    /**
     * Gets the unique ID of the player.
     *
     * @return Player's universally unique identifier.
     */
    UUID getUuid();

    /**
     * Gets the name of the player.
     *
     * @return Player name.
     */
    String getName();

    /**
     * Gets the role of the player in the {@link com.ultimismc.parties.api.party.Party}
     *
     * @return Player role.
     */
    PartyRole getRole();

    /**
     * Changes the role of the player in the party.
     *
     * @param role Player's new role.
     */
    void setRole(PartyRole role);

}
