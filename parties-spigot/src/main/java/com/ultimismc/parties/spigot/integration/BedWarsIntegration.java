package com.ultimismc.parties.spigot.integration;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.player.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BedWarsIntegration implements com.andrei1058.bedwars.api.party.Party {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public boolean hasParty(Player player) {
        return api.getPartiesManager().getParty(player.getUniqueId()) != null;
    }

    @Override
    public int partySize(Player player) {
        return api.getPartiesManager().getParty(player.getUniqueId()).getMembers().size();
    }

    @Override
    public boolean isOwner(Player player) {
        if (!hasParty(player))
            return false;
        return api.getPartiesManager().getParty(player.getUniqueId())
                .getOwner().getUuid().equals(player.getUniqueId());
    }

    @Override
    public List<Player> getMembers(Player player) {
        List<Player> players = new ArrayList<>();
        for (PartyPlayer partyPlayer : api.getPartiesManager().getParty(player.getUniqueId()).getMembers()) {
            Player player1 = Bukkit.getPlayer(partyPlayer.getUuid());
            if (player1 != null && player1.isOnline())
                players.add(player1);
        }
        return players;
    }

    @Override
    public void createParty(Player player, Player... players) {
    }

    @Override
    public void addMember(Player player, Player player1) {
    }

    @Override
    public void removeFromParty(Player player) {
    }

    @Override
    public void disband(Player player) {
    }

    @Override
    public boolean isMember(Player player, Player player1) {
        if (!hasParty(player))
            return false;
        return api.getPartiesManager().getParty(player.getUniqueId()).isMember(player1.getUniqueId());
    }

    @Override
    public void removePlayer(Player player, Player player1) {
    }

    @Override
    public boolean isInternal() {
        return false;
    }
}
