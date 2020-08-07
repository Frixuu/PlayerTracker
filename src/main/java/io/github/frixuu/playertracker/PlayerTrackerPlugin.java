package io.github.frixuu.playertracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import org.bukkit.plugin.java.JavaPlugin;

import io.github.frixuu.playertracker.config.PlayerTrackerConfig;

/**
 * This plugin, which makes players' compasses track other people.
 */
public class PlayerTrackerPlugin extends JavaPlugin {

    /** Synchronous task updating the compasses. */
    private CompassUpdaterRunnable compassUpdater;
    /** This plugin's config. */
    private PlayerTrackerConfig config;

    @Override
    public void onEnable() {
        // Try to load config file
        File configFile = new File(getDataFolder(), "config.json");
        if (!Files.exists(configFile.toPath())) {
            getLogger().info("Config does not exist. Creating a new one.");
            saveResource(configFile.getName(), false);
        }
        try {
            config = new Gson().fromJson(new FileReader(configFile), PlayerTrackerConfig.class);
        } catch (JsonSyntaxException e) {
            getLogger().severe("Malformed config file. "
            + "Correct it or delete to create a fresh one.");
        } catch (JsonIOException e) {
            getLogger().severe("Could not read the config file. "
            + "Please make sure it is not corrupted and has sufficient permissions set.");
        } catch (FileNotFoundException e) {
			getLogger().severe("The config file mysteriously disappeared.");
		}
        // Set up a repeating task
        compassUpdater = new CompassUpdaterRunnable(this);
        compassUpdater.runTaskTimer(this, 0, config.updateTickInterval);
        getLogger().info("Frixu's PlayerTracker is enabled!");
    }


    @Override
    public void onDisable() {
        compassUpdater.cancel();
        getLogger().info("Frixu's PlayerTracker is disabled!");
    }

    /**
     * Gets the plugin config.
     */
    public PlayerTrackerConfig getTrackerConfig() {
        return config;
    }

}