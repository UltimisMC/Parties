package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;

public class PromoteSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 1) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party promote <player>"
                    )
            );
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
        if (!partyPlayer.getUuid().equals(party.getOwner().getUuid())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou must be the &fParty Owner&c to use this command."
                    )
            );
            return;
        }
        Optional<Player> optional = PartiesVelocity.getInstance().getServer().getPlayer(args[0]);
        if (!optional.isPresent()) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cThis player is not online."
                    )
            );
            return;
        }
        Player target = optional.get();
        if (player == target) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou can't use this action on yourself!"
                    )
            );
            return;
        }
        if (!party.isMember(target.getUniqueId())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cThis player is not a member on the party."
                    )
            );
            return;
        }
        PartyPlayer partyTarget = party.getMember(target.getUniqueId());
        PartyRole newRole = partyTarget.getRole().getNext();
        if (newRole == partyTarget.getRole()) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cThis player already have the highest rank."
                    )
            );
            return;
        }
        partyTarget.setRole(newRole);
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(target.getUniqueId()) + target.getUsername()
                                + "&f got promoted to &e" + newRole.name().charAt(0) + newRole.name().substring(1).toLowerCase()
                                + "&f by &7" + Utils.getColor(player.getUniqueId()) + player.getUsername()
                ));
            }
        });
    }
}
