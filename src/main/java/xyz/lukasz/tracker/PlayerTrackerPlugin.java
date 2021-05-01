package xyz.lukasz.tracker;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import xyz.lukasz.tracker.config.PlayerTrackerConfig;
import xyz.lukasz.tracker.util.Compasses;
import xyz.lukasz.tracker.util.CustomCacheLoader;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.UUID;

/**
 * This plugin makes players' compasses track other people.
 */
public class PlayerTrackerPlugin extends JavaPlugin {

    /** Synchronous task updating the compasses. */
    private BukkitRunnable compassUpdater;

    /** This plugin's config. */
    @Getter private PlayerTrackerConfig trackerConfig;

    /**
     * Contains current mode information about players' compasses.
     */
    @Getter private final LoadingCache<UUID, Pair<TrackerMode, UUID>> modeMapping
        = CacheBuilder.newBuilder().build(
            ((CustomCacheLoader<UUID, Pair<TrackerMode, UUID>>)
                _uuid -> Pair.of(TrackerMode.NEAREST_PLAYER, null))
                .into());

    @Override
    public void onEnable() {
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
            MetricsLite metrics = new MetricsLite(this, pluginId);
        }

        compassUpdater = new BukkitRunnable() {
            @Override
            public void run() {
                if (getTrackerConfig() != null) {
                    getServer().getOnlinePlayers()
                        .forEach(player -> Compasses.update(player, getTrackerConfig()));
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