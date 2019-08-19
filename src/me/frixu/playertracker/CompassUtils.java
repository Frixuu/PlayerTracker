package me.frixu.playertracker;

import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Aggregates multiple functions useful in
 * calculating player distance or updating inventories.
 */
public final class CompassUtils {
    
    /**
     * Gets the player the closest to another player.
     * @param player Player to query
     * @return The nearest player or null, if no one was found.
     */
    @Nullable
    private Player getNearestPlayer(Player player) {
        return player.getWorld().getPlayers().stream()
            .filter(candidate -> candidate != player)
            .filter(candidate -> !candidate.getGameMode().equals(GameMode.SPECTATOR))
            .filter(candidate -> !player.spigot().getHiddenPlayers().contains(candidate))
            .sorted(Comparator.comparing(
                candidate -> candidate.getLocation().distanceSquared(player.getLocation())))
            .findFirst().orElse(null);
    }
    
    /**
     * Updates the player's compass target
     * and shows them an action bar with details.
     * @param player The player who will have their compass updated.
     */
    private void updateCompass(Player player) {

        // If player is offline or doesn't have a compass, do nothing
        if (!player.isOnline() || !player.getInventory().contains(Material.COMPASS))
            return;
        
        Player nearestPlayer = getNearestPlayer(player);
        String compassName;

        // A player was found
        if (nearestPlayer != null) {
            Location nearestLocation = nearestPlayer.getLocation();
            player.setCompassTarget(nearestLocation);
            double distance = nearestLocation.distance(player.getLocation());
            compassName = ChatColor.YELLOW + ""
            + ChatColor.BOLD + "Gracz: "
            + ChatColor.WHITE + nearestPlayer.getName()
            + ChatColor.YELLOW + " "
            + ChatColor.BOLD + "Odleglosc: "
            + ChatColor.WHITE + String.format("%.1f", distance);
        }
        // No player was found
        else {
            compassName = ChatColor.YELLOW + ""
            + ChatColor.BOLD + "Gracz: "
            + ChatColor.WHITE + "Brak";
        }

        PlayerInventory inventory = player.getInventory();

        // If the player is holding a compass, update their action bar
        if (inventory.getItemInOffHand().getType().equals(Material.COMPASS)
        || inventory.getItemInMainHand().getType().equals(Material.COMPASS)) {
            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(compassName));
        }
    }

    /**
     * Update all compasses on the server.
     * @param server The server to update.
     */
    public void updateServer(Server server) {
        for (Player p : server.getOnlinePlayers()) {
            updateCompass(p);
        }
    }
}