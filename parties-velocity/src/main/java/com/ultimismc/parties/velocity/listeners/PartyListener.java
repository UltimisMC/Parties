package com.ultimismc.parties.velocity.listeners;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.util.Utils;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PartyListener {

    @Subscribe
    public void onSwitch(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        Party party = ApiProvider.getApi().getPartiesManager().getParty(player.getUniqueId());
        if (party != null) {
            if (party.getOwner().getUuid().equals(player.getUniqueId())) {
                PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                    if (party.isMember(online.getUniqueId()) && online != player) {
                        online.createConnectionRequest(event.getServer()).connect();
                    }
                });
            }
        }
    }

    @Subscribe
    public void onQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        Party party = ApiProvider.getApi().getPartiesManager().getParty(player.getUniqueId());
        if (party != null) {
            if (party.getOwner().getUuid().equals(player.getUniqueId())) {
                PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                    if (party.isMember(online.getUniqueId())) {
                        online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                "&9&l[PARTY] &fParty got abandoned because &e" + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                        + "&f disconnected from the server."
                        ));
                    }
                });
                party.disband();
            } else {
                party.removeMember(party.getMember(player.getUniqueId()));
                PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                    if (party.isMember(online.getUniqueId())) {
                        online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                "&9&l[PARTY] " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                        + "&f has disconnected from the party!"
                        ));
                    }
                });
                if (party.getMembers().size() < 2) {
                    PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                        if (party.isMember(online.getUniqueId())) {
                            online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                    "&9&l[PARTY] &fParty is abandoned because of not enough players!"
                            ));
                        }
                    });
                    party.disband();
                }
            }
        }
    }

}
