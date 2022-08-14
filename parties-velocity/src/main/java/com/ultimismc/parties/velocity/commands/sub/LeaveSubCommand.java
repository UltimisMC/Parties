package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LeaveSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 0) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cUsage: /party leave"
            ));
            return;
        }
        Party party = api.getPartiesManager().getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cYou are not in party!"
            ));
            return;
        }
        PartyPlayer partyPlayer = party.getMember(player.getUniqueId());
        if (partyPlayer == party.getOwner()) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cPlease use /party disband to abandon your party!"
            ));
            return;
        }
        party.removeMember(partyPlayer);
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                + "&f has left the party!"
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
