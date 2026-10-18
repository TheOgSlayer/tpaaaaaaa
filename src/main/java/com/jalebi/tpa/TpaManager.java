package com.jalebi.tpa;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class TpaManager {

    public static final TextColor BLUE = TextColor.fromHexString("#00a2ff");
    public static final TextColor GOLD = TextColor.fromHexString("#ffa31a");
    private static final int COUNTDOWN = 5;
    private static final long EXPIRE_MS = 60_000L;

    public record Request(UUID from, UUID to, boolean here, long time) {}

    private final JalebiTpa plugin;
    private final Map<UUID, Request> pending = new HashMap<>(); // keyed by target
    private final Set<UUID> teleporting = new HashSet<>();

    public TpaManager(JalebiTpa plugin) {
        this.plugin = plugin;
    }

    public static Component msg(String text) {
        return Component.text(SmallCaps.of(text), NamedTextColor.WHITE);
    }

    public static Component prefix() {
        return Component.text(SmallCaps.of("jalebi"), GOLD)
                .append(Component.text(SmallCaps.of(" smp"), NamedTextColor.WHITE))
                .append(Component.text(" » ", NamedTextColor.DARK_GRAY));
    }

    public void sendRequest(Player from, Player to, boolean here) {
        if (!to.isOnline()) {
            from.sendMessage(prefix().append(msg("that player is no longer online.")));
            return;
        }
        pending.put(to.getUniqueId(), new Request(from.getUniqueId(), to.getUniqueId(), here, System.currentTimeMillis()));

        from.sendMessage(prefix().append(msg("request sent to "))
                .append(Component.text(to.getName(), BLUE)));

        String what = here ? " wants you to teleport to them." : " wants to teleport to you.";
        Component accept = Component.text("[" + SmallCaps.of("accept") + "]", NamedTextColor.GREEN, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpaccept"));
        Component deny = Component.text("[" + SmallCaps.of("deny") + "]", NamedTextColor.RED, TextDecoration.BOLD)
                .clickEvent(ClickEvent.runCommand("/tpacancel"));

        to.sendMessage(prefix().append(Component.text(from.getName(), BLUE)).append(msg(what)));
        to.sendMessage(Component.text("   ").append(accept).append(Component.text("  ")).append(deny));
    }

    public void accept(Player target) {
        Request r = pending.remove(target.getUniqueId());
        if (r == null || System.currentTimeMillis() - r.time() > EXPIRE_MS) {
            target.sendMessage(prefix().append(msg("you have no pending requests.")));
            return;
        }
        Player from = Bukkit.getPlayer(r.from());
        if (from == null) {
            target.sendMessage(prefix().append(msg("the requester is no longer online.")));
            return;
        }
        Player mover = r.here() ? target : from;
        Player dest = r.here() ? from : target;

        if (!teleporting.add(mover.getUniqueId())) {
            target.sendMessage(prefix().append(msg("that player is already teleporting.")));
            return;
        }
        from.sendMessage(prefix().append(Component.text(target.getName(), BLUE)).append(msg(" accepted your request.")));
        target.sendMessage(prefix().append(msg("request accepted.")));
        startCountdown(mover, dest, from, target);
    }

    public void deny(Player target) {
        Request r = pending.remove(target.getUniqueId());
        if (r == null || System.currentTimeMillis() - r.time() > EXPIRE_MS) {
            target.sendMessage(prefix().append(msg("you have no pending requests.")));
            return;
        }
        target.sendMessage(prefix().append(msg("request denied.")));
        Player from = Bukkit.getPlayer(r.from());
        if (from != null) {
            from.sendMessage(prefix().append(Component.text(target.getName(), BLUE)).append(msg(" denied your request.")));
        }
    }

    private Component countdownBar(int left) {
        return Component.text(SmallCaps.of("teleporting in "), NamedTextColor.WHITE)
                .append(Component.text(left, BLUE))
                .append(Component.text(SmallCaps.of(" seconds"), NamedTextColor.WHITE));
    }

    private void startCountdown(Player mover, Player dest, Player a, Player b) {
        new BukkitRunnable() {
            int left = COUNTDOWN;

            @Override
            public void run() {
                if (!mover.isOnline() || !dest.isOnline()) {
                    teleporting.remove(mover.getUniqueId());
                    cancel();
                    return;
                }
                if (left <= 0) {
                    mover.teleport(dest.getLocation());
                    teleporting.remove(mover.getUniqueId());
                    cancel();
                    return;
                }
                Component bar = countdownBar(left);
                a.sendActionBar(bar);
                if (!a.equals(b)) b.sendActionBar(bar);
                left--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
