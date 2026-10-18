package com.jalebi.tpa;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

public final class TpaCommands implements CommandExecutor, TabCompleter {

    private final TpaManager manager;

    public TpaCommands(TpaManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        switch (cmd.getName().toLowerCase()) {
            case "tpa", "tpahere" -> {
                boolean here = cmd.getName().equalsIgnoreCase("tpahere");
                if (args.length != 1) {
                    player.sendMessage(TpaManager.prefix().append(TpaManager.msg("usage: /" + cmd.getName() + " <player>")));
                    return true;
                }
                Player target = Bukkit.getPlayerExact(args[0]);
                if (target == null) {
                    player.sendMessage(TpaManager.prefix().append(TpaManager.msg("player not found.")));
                    return true;
                }
                if (target.equals(player)) {
                    player.sendMessage(TpaManager.prefix().append(TpaManager.msg("you can't send a request to yourself.")));
                    return true;
                }
                new TpaGui(target.getUniqueId(), here).open(player, target);
            }
            case "tpaccept" -> manager.accept(player);
            case "tpacancel" -> manager.deny(player);
            default -> { return false; }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        String n = cmd.getName().toLowerCase();
        if ((n.equals("tpa") || n.equals("tpahere")) && args.length == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .filter(s -> !s.equals(sender.getName()))
                    .toList();
        }
        return List.of();
    }
}
