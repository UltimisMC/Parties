package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.PartyRole;
import com.ultimismc.parties.api.player.PartyPlayer;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ChatSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length == 0) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party chat <player>"
                    )
            );
            return;
        }
        PartyWrapper party = (PartyWrapper) api.getPartiesManager().getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou are not in party!"
                    )
            );
            return;
        }
        if (party.isMuted() && party.getMember(player.getUniqueId()).getRole().getPermissionLevel() < 2) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cParty chat is currently disabled!"
                    )
            );
            return;
        }
        PartyPlayer partyPlayer = party.getMember(player.getUniqueId());
        String prefix = partyPlayer.getRole() == PartyRole.LEADER
                ? "&e✬ " :
                partyPlayer.getRole() == PartyRole.MODERATOR
                ? "&b✬ " : "";
        String message = String.join(" ", args);
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + prefix + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                + "&8 »&f "
                ).append(LegacyComponentSerializer.legacyAmpersand().deserialize(message)));
            }
        });
    }
}
