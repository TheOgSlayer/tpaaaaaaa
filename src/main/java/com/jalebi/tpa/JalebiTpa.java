package com.jalebi.tpa;

import org.bukkit.plugin.java.JavaPlugin;

public final class JalebiTpa extends JavaPlugin {

    private TpaManager manager;

    @Override
    public void onEnable() {
        manager = new TpaManager(this);
        TpaCommands commands = new TpaCommands(manager);
        for (String name : new String[]{"tpa", "tpahere", "tpaccept", "tpacancel"}) {
            getCommand(name).setExecutor(commands);
            getCommand(name).setTabCompleter(commands);
        }
        getServer().getPluginManager().registerEvents(new TpaGuiListener(manager), this);
    }
}
