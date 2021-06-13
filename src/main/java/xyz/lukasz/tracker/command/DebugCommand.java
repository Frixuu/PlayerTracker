package xyz.lukasz.tracker.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.lukasz.tracker.PlayerTrackerPlugin;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class DebugCommand implements CommandExecutor {

    private final PlayerTrackerPlugin plugin;
    private final Server server;
    private final Logger logger;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("Debug info logged!");
        return true;
    }
}
