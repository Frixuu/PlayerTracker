package io.github.frixuu.playertracker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;

import static io.github.frixuu.playertracker.CompassUtils.updateCompass;

/**
 * This plugin makes players' compasses track other people.
 */
public class PlayerTrackerPlugin extends JavaPlugin {

    /** Synchronous task updating the compasses. */
    private BukkitRunnable compassUpdater;
    /** This plugin's config. */
    @Getter private PlayerTrackerConfig trackerConfig;

    @Override
    public void onEnable() {
        // Try to load config file
        File configFile = new File(getDataFolder(), PlayerTrackerConfig.FILENAME);
        if (!Files.exists(configFile.toPath())) {
            getLogger().info("Config does not exist. Creating a new one.");
            saveResource(configFile.getName(), false);
        }
        try {
            Gson gson = new GsonBuilder().create();
            trackerConfig = gson.fromJson(new FileReader(configFile), PlayerTrackerConfig.class);
        } catch (JsonSyntaxException e) {
            getLogger().severe("Malformed config file. "
            + "Correct it or delete to create a fresh one.");
        } catch (JsonIOException e) {
            getLogger().severe("Could not read the config file. "
            + "Please make sure it is not corrupted and has sufficient permissions set.");
        } catch (FileNotFoundException e) {
			getLogger().severe("The config file mysteriously disappeared.");
		}

        if (trackerConfig != null && trackerConfig.isTelemetryActive()) {
            final int pluginId = 8456;
            Metrics metrics = new Metrics(this, pluginId);
        }

        // Set up a repeating task
        compassUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                if (getTrackerConfig() != null) {
                    getServer().getOnlinePlayers()
                        .forEach(player -> updateCompass(player, getTrackerConfig()));
                }
            }
        };
        compassUpdater.runTaskTimer(this, 0, getTrackerConfig().getUpdateTickInterval());
    }

    @Override
    public void onDisable() {
        compassUpdater.cancel();
        trackerConfig = null;
    }
}