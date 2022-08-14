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

public class WarpSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 0) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party warp"
                    )
            );
            return;
        }
        Party party = api.getPartiesManager().getParty(player.getUniqueId());
        if (party == null) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou are not in party!"
                    )
            );
            return;
        }
        PartyPlayer partyPlayer = party.getMember(player.getUniqueId());
        if (partyPlayer.getRole().getPermissionLevel() < 3) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou must be a &fParty Leader&c to use this command."
                    )
            );
            return;
        }
        if (!player.getCurrentServer().isPresent())
            return;
        player.sendMessage(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] &fTeleporting party members to &e"
                                + player.getCurrentServer().get().getServerInfo().getName() + "&f..."
                ));
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId()) && online != player) {
                online.sendMessage(
                        LegacyComponentSerializer.legacyAmpersand().deserialize(
                                "&9&l[PARTY] &e" + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                + "&f have warped party members to &e" + player.getCurrentServer().get().getServerInfo().getName()
                        ));
                online.createConnectionRequest(player.getCurrentServer().get().getServer()).connect();
            }
        });
    }

}
