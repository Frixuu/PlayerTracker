package io.github.frixuu.playertracker;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
import static org.bukkit.Material.COMPASS;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import net.md_5.bungee.api.chat.BaseComponent;
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
    private static Player getNearestPlayer(@NotNull Player player, PlayerTrackerConfig config) {
        Stream<Player> players = player.getWorld().getPlayers().stream();
        players = players.filter(other -> other != player);
        
        if (!config.tracker.trackSpectators)
        players = players.filter(other ->
            !other.getGameMode().equals(GameMode.SPECTATOR)
        );
        
        if (!config.tracker.trackHidden)
        players = players.filter(other ->
            !player.spigot().getHiddenPlayers().contains(other)
        );
        
        if (!config.tracker.trackInvisible)
        players = players.filter(other ->
            !other.getActivePotionEffects().stream()
                .anyMatch(e -> e.getType().equals(PotionEffectType.INVISIBILITY))
        );
        
        if (!config.tracker.trackTeamScoreboard)
        players = players.filter(other -> {
            Team playerTeam = player.getScoreboard().getEntryTeam(player.getName());
            if (playerTeam == null) return true;
            return !playerTeam.hasEntry(other.getName());
        });
        
        if (!config.tracker.trackSameColor)
        players = players.filter(other -> {
            String otherColor = other.getPlayerListName().replace(other.getName(), "");
            String myColor = player.getPlayerListName().replace(player.getName(), "");
            return !myColor.equals(otherColor);
        });
        
        return players.sorted(Comparator.comparing(
            candidate -> candidate.getLocation().distanceSquared(player.getLocation())))
                .findFirst().orElse(null);
    }
    
    /**
    * Updates the player's compass target
    * and shows them an action bar with details.
    * @param player The player who will have their compass updated.
    */
    public static void updateCompass(@NotNull Player player, PlayerTrackerConfig config) {
        
        // If player is offline or doesn't have a compass, do nothing
        if (!player.isOnline() || !player.getInventory().contains(COMPASS))
        return;
        
        Player nearestPlayer = getNearestPlayer(player, config);
        String trackerText = config.messages.templateMissing;
        
        // A player was found
        if (nearestPlayer != null) {
            Location nearestLocation = nearestPlayer.getLocation();
            player.setCompassTarget(nearestLocation);
            double distance = nearestLocation.distance(player.getLocation());
            trackerText = config.messages.templateFound
            .replace("{{name}}", nearestPlayer.getPlayerListName())
            .replace("{{distance}}", String.format("%.1f", distance))
            .replace("{{distanceft}}", String.format("%.1f", distance * 3.28084f));
        }
        
        PlayerInventory inventory = player.getInventory();
        final String messageText = ChatColor.translateAlternateColorCodes('%', trackerText);
        final BaseComponent[] message = TextComponent.fromLegacyText(messageText);
        
        switch (config.displayMethod) {
            case ACTION_BAR:
            if (inventory.getItemInOffHand().getType().equals(COMPASS)
            || inventory.getItemInMainHand().getType().equals(COMPASS)) {
                player.spigot().sendMessage(ACTION_BAR, message);
            }
            break;
            case ITEM_NAME:
            Arrays.stream(inventory.getContents())
            .filter(item -> item != null && item.getType().equals(COMPASS))
            .forEach(item -> {
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(messageText);
                item.setItemMeta(meta);
            });
            break;
        }
    }
    
    /**
    * Update all compasses on the server.
    */
    public static void updateServer(PlayerTrackerPlugin plugin) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            updateCompass(player, plugin.getTrackerConfig());
        }
    }
}