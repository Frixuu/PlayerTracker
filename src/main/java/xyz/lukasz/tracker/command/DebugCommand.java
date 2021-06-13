package xyz.lukasz.tracker.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.lukasz.tracker.ModeManager;
import xyz.lukasz.tracker.PlayerTrackerPlugin;
import xyz.lukasz.tracker.TrackerMode;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class DebugCommand implements CommandExecutor {

    private final PlayerTrackerPlugin plugin;
    private final ModeManager modeManager;
    private final Server server;
    private final Logger logger;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        final var builder = new StringBuilder();
        logger.info("-==== TRACKER DEBUG ====-");

        final var override = modeManager.getGlobalOverride();
        if (override == null) {
            logger.info("GLOBAL OVERRIDE - NONE");
        } else {
            builder.append("GLOBAL OVERRIDE - MODE ");
            final var mode = override.getMode();
            builder.append(mode.toString());
            if (mode == TrackerMode.TARGET_PLAYER) {
                builder.append(" - TARGETTING ");
                final var target = override.getCurrentTarget();
                if (target != null) {
                    builder.append(server.getPlayer(target).getDisplayName());
                } else {
                    builder.append("null");
                }
            }
            logger.info(builder.toString());
        }

        for (final var player : server.getOnlinePlayers()) {
            builder.setLength(0);
            builder.append(player.getDisplayName());
            final var settings = modeManager.getPlayerSettings(player);
            final var mode = settings.getMode();
            builder.append(" - mode ").append(mode.toString());
            if (mode == TrackerMode.TARGET_PLAYER) {
                builder.append(" - targetting ");
                final var target = settings.getCurrentTarget();
                if (target != null) {
                    builder.append(server.getPlayer(target).getDisplayName());
                } else {
                    builder.append("null");
                }
            }
            logger.info(builder.toString());
        }

        logger.info("  -==== END DEBUG ====-");

        sender.sendMessage("Debug info logged!");
        return true;
    }
}
