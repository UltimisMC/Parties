package com.ultimismc.parties.api.party;

import com.ultimismc.parties.api.player.PartyPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * @author AkramL
 * @since 1.0-BETA
 */
public interface Party {

    /**
     * Gets the Owner of the current
     * {@link Party} Object.
     * @return {@link Party} holder.
     */
    PartyPlayer getOwner();

    /**
     * Gets all the members on the
     * current {@link Party}
     * @return List of available {@link PartyPlayer} objects.
     */
    Collection<PartyPlayer> getMembers();

    /**
     * Checks if any member on the party holds the provided uuid
     * @param uuid The UUID of the player to check.
     * @return If {@link PartyPlayer} is a member.
     */
    default boolean isMember(UUID uuid) {
        return getMembers().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(uuid));
    }

    /**
     * Gets a member from the party via his UUID.
     * @param uuid The uuid of the player to get.
     * @return The provided {@link PartyPlayer} object.
     */
    default PartyPlayer getMember(UUID uuid) {
        return getMembers().stream().filter(partyPlayer -> partyPlayer.getUuid().equals(uuid))
                .findFirst().orElse(null);
    }

    /**
     * Adds a Member to the current {@link Party} object.
     * @param partyPlayer The player to add.
     * @return If player got successfully added.
     */
    boolean addMember(PartyPlayer partyPlayer);

    /**
     * Removes a Member from the current {@link Party} object.
     * @param partyPlayer The player to remove.
     * @return If  player got successfully removed.
     */
    boolean removeMember(PartyPlayer partyPlayer);

    /**
     * Used to check if the party is public
     * so players can join without an invitation.
     * @return Is the party public.
     */
    boolean isOpen();

    /**
     * Changes the state of the party if
     * It's public or not.
     * @param open New party state.
     */
    void setOpen(boolean open);

    /**
     * Checks if the party has private games
     * option enabled or not.
     * @return Private game state.
     */
    boolean isPrivateGame();

    /**
     * Changes the state of the private games
     * in the party.
     * @param privateGame New private game state.
     */
    void setPrivateGame(boolean privateGame);

    /**
     * Abandons the party and revokes it.
     */
    void disband();

}