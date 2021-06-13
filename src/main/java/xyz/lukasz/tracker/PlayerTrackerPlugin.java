package xyz.lukasz.tracker;

import lombok.Getter;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import xyz.lukasz.tracker.command.DebugCommand;
import xyz.lukasz.tracker.config.PluginConfig;
import xyz.lukasz.tracker.event.PlayerInteractListener;
import xyz.lukasz.tracker.util.Compasses;
import xyz.lukasz.tracker.util.Runnables;

import java.io.File;
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
    @Getter private PluginConfig pluginConfig;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        final var configFile = new File(getDataFolder(), "config.yml");
        try {
            final var configReader = new FileReader(configFile);
            final var yaml = new Yaml(new CustomClassLoaderConstructor(PluginConfig.class.getClassLoader()));
            pluginConfig = yaml.loadAs(configReader, PluginConfig.class);
        } catch (Exception e) {
            getLogger().severe("Cannot load config: " + e.getMessage());
            e.printStackTrace();
            getPluginLoader().disablePlugin(this);
            return;
        }

        if (pluginConfig != null && pluginConfig.getTechnical().isBStats()) {
            final int pluginId = 8456;
            final var _metrics = new MetricsLite(this, pluginId);
        }

        final var modeManager = new ModeManager();

        final var pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(
            new PlayerInteractListener(getPluginConfig(), modeManager, getServer(), getLogger()),
            this);

        getCommand("trackerdebug").setExecutor(
            new DebugCommand(this, modeManager, getServer(), getLogger()));

        compassUpdater = Runnables.bukkitRunnable(() -> {
            if (getPluginConfig() != null) {
                getServer().getOnlinePlayers()
                    .forEach(player -> Compasses.update(
                        player, getPluginConfig(),
                        modeManager.getPlayerSettings(player)));
            }
        });

        compassUpdater.runTaskTimer(this, 0,
            pluginConfig.getTechnical().getUpdateInterval());
    }

    @Override
    public void onDisable() {
        if (compassUpdater != null) {
            compassUpdater.cancel();
        }
        pluginConfig = null;
    }
}