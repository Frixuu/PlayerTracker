package me.frixu.playertracker;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTracker extends JavaPlugin {

    private CompassUtils utils;
    private Server server;
    private BukkitRunnable compassUpdater;

    @Override
    public void onEnable() {
        server = getServer();
        utils = new CompassUtils();
        compassUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                // This currently does a lot of calculations.
                // Use only on smaller player count servers.
                utils.updateServer(server);
            }
        };
        compassUpdater.runTaskTimer(this, 0, 15);
        getLogger().info("Frixu's PlayerTracker is enabled!");
    }


    @Override
    public void onDisable() {
        compassUpdater.cancel();
        getLogger().info("Frixu's PlayerTracker is disabled!");
    }
}