package io.github.frixuu.playertracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;

/**
 * This plugin, which makes players' compasses track other people.
 */
public class PlayerTrackerPlugin extends JavaPlugin {

    /** Synchronous task updating the compasses. */
    private BukkitRunnable compassUpdater;
    /** This plugin's config. */
    @Getter private PlayerTrackerConfig config;

    @Override
    public void onEnable() {
        // Try to load config file
        File configFile = new File(getDataFolder(), "config.json");
        if (!Files.exists(configFile.toPath())) {
            getLogger().info("Config does not exist. Creating a new one.");
            saveResource(configFile.getName(), false);
        }
        try {
            Gson gson = new GsonBuilder().create();
            config = gson.fromJson(new FileReader(configFile), PlayerTrackerConfig.class);
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
        compassUpdater = new BukkitRunnable() {
            @Override public void run() {
                CompassUtils.updateServer(PlayerTrackerPlugin.this);
            }
        };
        compassUpdater.runTaskTimer(this, 0, getConfig().updateTickInterval);
    }

    @Override
    public void onDisable() {
        compassUpdater.cancel();
    }
}