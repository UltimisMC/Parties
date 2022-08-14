package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;

public class DenySubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 1) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party join <player>"
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
        PartyWrapper party = (PartyWrapper) api.getPartiesManager().getParty(target.getUniqueId());
        if (party == null || party.getOwner().getUuid() != target.getUniqueId()) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cThis player is not in party!"
                    )
            );
            return;
        }
        if (!party.getInvited().contains(player.getUniqueId())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou are not invited to this party!"
                    )
            );
            return;
        }
        party.getInvited().remove(target.getUniqueId());
        player.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] &fSuccessfully declined party invite to " + Utils.getColor(target.getUniqueId())
                        + target.getUsername() + "&f's party."
                )
        );
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                + "&f declined the invite for this party!"
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
