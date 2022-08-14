package com.ultimismc.parties.velocity.commands;

import com.velocitypowered.api.command.CommandSource;

public interface SubCommand {

    void execute(CommandSource source, String[] args);

}
