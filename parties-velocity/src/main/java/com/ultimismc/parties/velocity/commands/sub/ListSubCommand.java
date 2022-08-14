package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ListSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 0) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cUsage: /party list"
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
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&9&l[PARTY] &fThere is currently &e" + party.getMembers().size()
                        + "&f players on " + party.getOwner().getName() + "'s party."
        ));
        StringBuilder leader = new StringBuilder(), mod = new StringBuilder(), member = new StringBuilder();
        for (PartyPlayer partyPlayer : party.getMembers()) {
            if (partyPlayer.getRole() == PartyRole.LEADER) {
                leader.append("&7, ").append(Utils.getColor(partyPlayer.getUuid())).append(partyPlayer.getName());
            }
            if (partyPlayer.getRole() == PartyRole.MODERATOR) {
                mod.append("&7, ").append(Utils.getColor(partyPlayer.getUuid())).append(partyPlayer.getName());
            }
            if (partyPlayer.getRole() == PartyRole.MEMBER) {
                member.append("&7, ").append(Utils.getColor(partyPlayer.getUuid())).append(partyPlayer.getName());
            }
        }
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&f&l[LEADER] " + leader.toString().replaceFirst("&7, ", "")
        ));
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&e&l[MODERATOR] " + mod.toString().replaceFirst("&7, ", "")
        ));
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&7&l[MEMBER] " + member.toString().replaceFirst("&7, ", "")
        ));
    }

}
