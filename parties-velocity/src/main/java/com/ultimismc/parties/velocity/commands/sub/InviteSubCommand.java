package com.ultimismc.parties.velocity.commands.sub;

import com.ultimismc.parties.api.ApiProvider;
import com.ultimismc.parties.api.PartiesAPI;
import com.ultimismc.parties.velocity.PartiesVelocity;
import com.ultimismc.parties.velocity.commands.SubCommand;
import com.ultimismc.parties.velocity.util.Utils;
import com.ultimismc.parties.velocity.wrapper.PartyPlayerWrapper;
import com.ultimismc.parties.velocity.wrapper.PartyWrapper;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InviteSubCommand implements SubCommand {

    private final PartiesAPI api = ApiProvider.getApi();

    @Override
    public void execute(CommandSource source, String[] args) {
        Player player = (Player) source;
        if (args.length == 0) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cUsage: /party invite <player>"
                    )
            );
            return;
        }
        final PartyWrapper party = (PartyWrapper) (api.getPartiesManager().getParty(player.getUniqueId()) != null ?
                api.getPartiesManager().getParty(player.getUniqueId())
                : api.getPartiesManager().createFor(new PartyPlayerWrapper(player.getUniqueId(), player.getUsername())));
        if (party.getMember(player.getUniqueId()).getRole().getPermissionLevel() < 3) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou must be a &fParty Leader&c to use this command."
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
        if (party.isOpen()) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou don't need to invite the player since it's a public party!"
                    )
            );
            return;
        }
        if (api.getPartiesManager().getParty(target.getUniqueId()) != null) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cThis player is already in a party!"
                    )
            );
            return;
        }
        if (party.getInvited().contains(target.getUniqueId())) {
            player.sendMessage(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(
                            "&9&l[PARTY] &cYou already invited this player to your party!"
                    )
            );
            return;
        }
        Component accept = LegacyComponentSerializer.legacyAmpersand()
                .deserialize("&e&l[ACCEPT]")
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + party.getOwner().getName()))
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(" "))
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize("&c&l[DECLINE]")
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + party.getOwner().getName())));
        target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&9&l[PARTY] &e" + Utils.getColor(player.getUniqueId()) + player.getUsername() + "&f invited you to join &e"
                        + Utils.getColor(party.getOwner().getUuid()) + party.getOwner().getName() + "&f's party," +
                        " You have &e60 second(s) &fto join."
        ));
        target.sendMessage(accept);
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&9&l[PARTY] &fSuccessfully invited " + Utils.getColor(target.getUniqueId()) + target.getUsername()
                + "&f to join this party, they have &e60 second(s)&f to join."
        ));
        party.getInvited().add(target.getUniqueId());
        AtomicInteger secPassed = new AtomicInteger();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(() -> {
            int currentSec = secPassed.getAndIncrement();
            if (api.getPartiesManager().getParty(party.getOwner().getUuid()) == null
                    || party.isOpen() || !party.getInvited().contains(target.getUniqueId())
                    || !PartiesVelocity.getInstance().getServer().getPlayer(target.getUniqueId()).isPresent()) {
                service.shutdownNow();
                party.getInvited().remove(target.getUniqueId());
                if (party.getMembers().size() < 2 && party.getInvited().isEmpty()) {
                    PartiesVelocity.getInstance().getServer().getAllPlayers().forEach(online -> {
                        if (party.isMember(online.getUniqueId())) {
                            online.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                    "&9&l[PARTY] &fParty is abandoned because of not enough players!"
                            ));
                        }
                    });
                    party.disband();
                }
                return;
            }
            if (currentSec >= 60) {
                target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] &fParty invitation for &c"
                                + Utils.getColor(party.getOwner().getUuid())
                                + party.getOwner().getName() + "&f has been expired."
                ));
                party.getInvited().remove(target.getUniqueId());
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        "&9&l[PARTY] &fYour invitation to &c" + Utils.getColor(target.getUniqueId())
                                + target.getUsername() + "&f is expired."
                ));
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
                service.shutdown();
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

}
