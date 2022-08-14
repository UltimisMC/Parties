package com.ultimismc.parties.velocity.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.UUID;

public class Utils {

    public static String getColor(UUID uuid) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(uuid);
        if (user == null)
            return "&7";
        String suffix = user.getCachedData().getMetaData().getSuffix();
        return suffix != null ? suffix : "&7";
    }
}
