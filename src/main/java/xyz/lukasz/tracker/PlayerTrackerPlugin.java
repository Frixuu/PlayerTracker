package xyz.lukasz.tracker;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.lukasz.tracker.command.DebugCommand;
import xyz.lukasz.tracker.config.PlayerTrackerConfig;
import xyz.lukasz.tracker.event.PlayerInteractListener;
import xyz.lukasz.tracker.util.Compasses;
import xyz.lukasz.tracker.util.Runnables;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * This plugin makes players' compasses track other people.
 */
public class PlayerTrackerPlugin extends JavaPlugin {

    /**
     * Synchronous task updating the compasses.
     */
    private BukkitRunnable compassUpdater;

    /**
     * This plugin's config.
     */
    @Getter private PlayerTrackerConfig pluginConfig;

    @Override
    public void onEnable() {
        final var configFile = new File(getDataFolder(), PlayerTrackerConfig.FILENAME);
        if (!configFile.exists()) {
            getLogger().info("Config does not exist. Creating a new one.");
            saveResource(configFile.getName(), false);
        }
        try {
            final var gson = new GsonBuilder().serializeNulls().create();
            pluginConfig = gson.fromJson(new FileReader(configFile), PlayerTrackerConfig.class);
        } catch (JsonSyntaxException e) {
            getLogger().severe("Malformed config file. "
            + "Correct it or delete to create a fresh one.");
        } catch (JsonIOException e) {
            getLogger().severe("Could not read the config file. "
            + "Please make sure it is not corrupted and has sufficient permissions set.");
        } catch (FileNotFoundException e) {
			getLogger().severe("The config file mysteriously disappeared.");
		}

        if (pluginConfig != null && pluginConfig.isTelemetryActive()) {
            final int pluginId = 8456;
            final var _metrics = new MetricsLite(this, pluginId);
        }

        final var modeManager = new ModeManager();

        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(
            new PlayerInteractListener(getPluginConfig(), modeManager, getServer(), getLogger()),
            this);

        getCommand("trackerdebug").setExecutor(new DebugCommand(this, getServer(), getLogger()));

        compassUpdater = Runnables.bukkitRunnable(() -> {
            if (getPluginConfig() != null) {
                getServer().getOnlinePlayers()
                    .forEach(player -> Compasses.update(
                        player, getPluginConfig(),
                        modeManager.getPlayerSettings(player)));
            }
        });

        compassUpdater.runTaskTimer(this, 0, getPluginConfig().getUpdateTickInterval());
    }

    @Override
    public void onDisable() {
        compassUpdater.cancel();
        pluginConfig = null;
    }
}