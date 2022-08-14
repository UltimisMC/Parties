package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.api.party.Party;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MuteSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();


    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length != 0) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party mute"
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
        if (party.getMember(player.getUniqueId()).getRole().getPermissionLevel() < 2) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou must be a &fParty Moderator&c or higher to use this command."
                    )
            );
            return;
        }
        party.setMuted(!party.isMuted());
        PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
            if (party.isMember(online.getUniqueId())) {
                online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] " + Utils.getColor(player.getUniqueId()) + player.getUsername()
                                + "&f have" + (party.isMuted() ? "&c muted" : "&e unmuted") + "&f party chat!"
                ));
            }
        });
    }
}