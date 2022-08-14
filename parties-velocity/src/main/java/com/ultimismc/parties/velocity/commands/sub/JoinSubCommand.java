package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyPlayerWrapper;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;

public class JoinSubCommand implements SubCommand {

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
        if (api.getPartiesManager().getParty(player.getUniqueId()) != null) {
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cYou are already in a party!"
            ));
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
        if ((!party.isOpen() && !party.getInvited().contains(player.getUniqueId()))
                || party.getBanned().contains(player.getUniqueId())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou are not allowed to join this party!"
                    )
            );
            return;
        }
        party.getInvited().remove(player.getUniqueId());
        PartyPlayer partyPlayer = new PartyPlayerWrapper(player.getUniqueId(), player.getUsername());
        partyPlayer.setRole(PartyRole.MEMBER);
        party.addMember(partyPlayer);
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                        + "&f has joined the party!"
                ));
            }
        });
    }

}
