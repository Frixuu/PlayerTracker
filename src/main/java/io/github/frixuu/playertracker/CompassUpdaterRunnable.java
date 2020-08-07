package io.github.frixuu.playertracker;

import org.bukkit.scheduler.BukkitRunnable;

/** Task that updates all compasses on a server. */
public class CompassUpdaterRunnable extends BukkitRunnable {

    /** This plugin. */
    private PlayerTrackerPlugin plugin;

    /**
     * Creates a new compass updating runnable.
     */
    public CompassUpdaterRunnable(PlayerTrackerPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    /**
     * Updates the compasses.
     */
    @Override
    public void run() {
        CompassUtils.updateServer(plugin);
    }
    
}