package com.ultimismc.parties.api.party;

import com.ultimismc.parties.api.player.PartyPlayer;

import java.util.UUID;

/**
 * @author AkramL
 * @since 1.0-BETA
 */
public interface PartiesManager {

    /**
     * Gets the party by the player.
     * @param partyPlayer Player to get from.
     * @return The provided {@link Party} for the player.
     */
    Party getParty(PartyPlayer partyPlayer);

    /**
     * Gets the party by the UUID.
     * @param uuid UUID of the Player to get from.
     * @return The provided {@link Party} for the player.
     */
    Party getParty(UUID uuid);

    /**
     * Creates the party for the provided player.
     * @param partyPlayer Player to create the party to.
     * @return The created party.
     */
    Party createFor(PartyPlayer partyPlayer);

    /**
     * Checks for if the player has a party.
     * @param partyPlayer Player to check.
     * @return If he is on a party.
     */
    boolean hasParty(PartyPlayer partyPlayer);

    /**
     * Removes the party from memory and database
     * if provided.
     * @param party Party to remove.
     */
    void removeParty(Party party);

}
