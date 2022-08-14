package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;

public class BanSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 1) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cUsage: /party ban <player>"
            ));
            return;
        }
        PartyWrapper party = (PartyWrapper) api.getPartiesManager().getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cYou are not in party!"
            ));
            return;
        }
        PartyPlayer partyPlayer = party.getMember(player.getUniqueId());
        if (partyPlayer.getRole().getPermissionLevel() < 2) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou must be a &fParty Moderator&c or higher to use this command."
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
        if (party.getOwner().getUuid().equals(target.getUniqueId())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou can't use this action with party owner!"
                    )
            );
            return;
        }
        if (party.getBanned().contains(target.getUniqueId())) {
            party.getBanned().remove(target.getUniqueId());
            PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                if (party.isMember(online.getUniqueId())) {
                    online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] " + Utils.getColor(target.getUniqueId()) + target.getUsername()
                                    + "&f got unbanned by " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                    ));
                }
            });
            return;
        }
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(target.getUniqueId()) + target.getUsername()
                                + "&f got banned by " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                ));
            }
        });
        party.getBanned().add(target.getUniqueId());
        if (party.isMember(target.getUniqueId())) {
            party.removeMember(party.getMember(target.getUniqueId()));
        }
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
