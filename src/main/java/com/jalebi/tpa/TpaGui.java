package com.jalebi.tpa;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public final class TpaGui implements InventoryHolder {

    public static final int CANCEL_SLOT = 10;
    public static final int DIMENSION_SLOT = 12;
    public static final int HEAD_SLOT = 13;
    public static final int STATS_SLOT = 14;
    public static final int SEND_SLOT = 16;

    private final UUID target;
    private final boolean here;
    private Inventory inventory;

    public TpaGui(UUID target, boolean here) {
        this.target = target;
        this.here = here;
    }

    public UUID target() { return target; }
    public boolean here() { return here; }

    @Override
    public Inventory getInventory() { return inventory; }

    private static Component name(String text, TextColor color) {
        return Component.text(SmallCaps.of(text), color).decoration(TextDecoration.ITALIC, false);
    }

    private static ItemStack item(Material m, Component name, Component... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(name);
        if (lore.length > 0) meta.lore(List.of(lore));
        it.setItemMeta(meta);
        return it;
    }

    public void open(Player viewer, Player targetPlayer) {
        Component title = Component.text(SmallCaps.of("jalebi"), TpaManager.GOLD)
                .append(Component.text(SmallCaps.of(" smp"), NamedTextColor.WHITE));
        inventory = Bukkit.createInventory(this, 27, title);

        inventory.setItem(CANCEL_SLOT, item(Material.RED_STAINED_GLASS_PANE,
                name("cancel", NamedTextColor.RED)));

        // Dimension block
        Material dimMat;
        String dimName;
        switch (targetPlayer.getWorld().getEnvironment()) {
            case NETHER -> { dimMat = Material.NETHERRACK; dimName = "nether"; }
            case THE_END -> { dimMat = Material.END_STONE; dimName = "the end"; }
            default -> { dimMat = Material.GRASS_BLOCK; dimName = "overworld"; }
        }
        inventory.setItem(DIMENSION_SLOT, item(dimMat,
                name("dimension", NamedTextColor.WHITE),
                name(dimName, TpaManager.GOLD)));

        // Player head
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(targetPlayer);
        sm.displayName(Component.text(targetPlayer.getName(), TpaManager.BLUE).decoration(TextDecoration.ITALIC, false));
        sm.lore(List.of(name(here ? "they will teleport to you" : "you will teleport to them", NamedTextColor.GRAY)));
        head.setItemMeta(sm);
        inventory.setItem(HEAD_SLOT, head);

        // Kills / deaths
        int kills = targetPlayer.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = targetPlayer.getStatistic(Statistic.DEATHS);
        inventory.setItem(STATS_SLOT, item(Material.FEATHER,
                name("stats", NamedTextColor.WHITE),
                name("kills: " + kills, NamedTextColor.GREEN),
                name("deaths: " + deaths, NamedTextColor.RED)));

        inventory.setItem(SEND_SLOT, item(Material.LIME_STAINED_GLASS_PANE,
                name("send", NamedTextColor.GREEN)));

        viewer.openInventory(inventory);
    }
}
