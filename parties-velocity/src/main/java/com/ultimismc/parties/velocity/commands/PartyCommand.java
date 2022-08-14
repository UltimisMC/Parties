package com.ultimismc.parties.velocity.commands;

import com.ultimismc.parties.velocity.commands.sub.*;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PartyCommand implements SimpleCommand {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public PartyCommand() {
        subCommands.put("invite", new InviteSubCommand());
        subCommands.put("join", new JoinSubCommand());
        subCommands.put("deny", new DenySubCommand());
        subCommands.put("leave", new LeaveSubCommand());
        subCommands.put("list", new ListSubCommand());
        subCommands.put("kick", new KickSubCommand());
        subCommands.put("ban", new BanSubCommand());
        subCommands.put("chat", new ChatSubCommand());
        subCommands.put("open", new OpenSubCommand());
        subCommands.put("private", new PrivateGamesSubCommand());
        subCommands.put("mute", new MuteSubCommand());
        subCommands.put("warp", new WarpSubCommand());
        subCommands.put("promote", new PromoteSubCommand());
        subCommands.put("demote", new DemoteSubCommand());
        subCommands.put("disband", new DisbandSubCommand());
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if (!(source instanceof Player))
            return;
        if (args.length == 0) {
            sendHelp(source);
            return;
        }
        if (args[0].equalsIgnoreCase("help")) {
            sendHelp(source);
            return;
        }
        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    "&9&l[PARTY] &cUnknown command, use \"/" + invocation.alias() + " help\" for help."
            ));
            return;
        }
        String[] subArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            subArgs[i-1] = args[i];
        }
        subCommand.execute(source, subArgs);
    }

    void sendHelp(CommandSource source) {
        Arrays.asList(
                "",
                "&b/party help &7- &fShow this help menu.",
                "&b/party invite <player> &7- &fInvite a player to your party.",
                "&b/party join <player> &7- &fJoin a player's party.",
                "&b/party deny <player> &7- &fDecline an invitation from this party.",
                "&b/party leave &7- &fLeave your current party.",
                "&b/party list &7- &fList the available players on the party.",
                "&b/party kick <player> &7- &fKick a player from your party.",
                "&b/party ban <player> &7- &fBan a player from your party.",
                "&b/party chat <message> &7- &fSends a message to party members.",
                "&b/party open &7- &fAllow everyone to join your party without invite.",
                "&b/party private &7- &fToggles private games for the party.",
                "&b/party mute &7- &fMute party chat.",
                "&b/party warp &7- &fWarp all players to your current server.",
                "&b/party promote <player> &7- &fPromote a player on your party.",
                "&b/party demote <player> &7- &fDemote a player on your party.",
                "&b/party disband &7- &fClose the party.",
                ""
        ).forEach(string -> source.sendMessage(
                LegacyComponentSerializer
                        .legacyAmpersand()
                        .deserialize(string)
        ));
    }
}
