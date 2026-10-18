package com.jalebi.tpa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class TpaGuiListener implements Listener {

    private final TpaManager manager;

    public TpaGuiListener(TpaManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof TpaGui gui)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player player)) return;
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        switch (e.getSlot()) {
            case TpaGui.CANCEL_SLOT -> player.closeInventory();
            case TpaGui.SEND_SLOT -> {
                player.closeInventory();
                Player target = Bukkit.getPlayer(gui.target());
                if (target == null) {
                    player.sendMessage(TpaManager.prefix().append(TpaManager.msg("that player is no longer online.")));
                    return;
                }
                manager.sendRequest(player, target, gui.here());
            }
            default -> {}
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof TpaGui) e.setCancelled(true);
    }
}
