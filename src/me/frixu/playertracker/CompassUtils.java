package me.frixu.playertracker;

import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class CompassUtils {
    
    private Player getNearestPlayer(Player p, Double range) {
        Player nearest = p.getWorld().getPlayers().stream()
            .filter(t -> t != p)
            .filter(t -> !t.getGameMode().equals(GameMode.SPECTATOR))
            .sorted(Comparator.comparing(t -> t.getLocation().distance(p.getLocation())))
            .findFirst().orElse(null);

        if (nearest == null) return null;
        if (nearest.getLocation().distance(p.getLocation()) > range) return null;
        return nearest;
    }
    
    private void updateCompass(Player p) {

        // If player is offline or doesn't have a compass, do nothing
        if (!p.isOnline() || !p.getInventory().contains(Material.COMPASS))
            return;
        
        Player nearestPlayer = getNearestPlayer(p, 500.0);
        String compassName;

        // A player was found within the range
        if (nearestPlayer != null) {
            Location nearestLocation = nearestPlayer.getLocation();
            p.setCompassTarget(nearestLocation);
            double distance = nearestLocation.distance(p.getLocation());
            compassName = ChatColor.YELLOW + ""
            + ChatColor.BOLD + "Gracz: "
            + ChatColor.WHITE + nearestPlayer.getName()
            + ChatColor.YELLOW + " "
            + ChatColor.BOLD + "Odleglosc: "
            + ChatColor.WHITE + String.format("%.1f", distance);
        }
        // No player was found in the range
        else {
            compassName = ChatColor.YELLOW + ""
            + ChatColor.BOLD + "Gracz: "
            + ChatColor.WHITE + "Brak";
        }
            
        // Update every compass in the player's inventory
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType().equals(Material.COMPASS)) {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(compassName);
                item.setItemMeta(meta);
            }
        }
    }

    public void updateServer(Server server) {
        for (Player p : server.getOnlinePlayers()) {
            updateCompass(p);
        }
    }
}