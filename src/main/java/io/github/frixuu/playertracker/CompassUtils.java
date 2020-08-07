package io.github.frixuu.playertracker;

import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static org.bukkit.Material.COMPASS;

/**
* Aggregates multiple functions useful in
* calculating player distance or updating inventories.
*/
public final class CompassUtils {

    private static final double FEET_IN_METER = 3.28084;

    /**
    * Gets the player the closest to another player.
    * @param player Player to query
    * @return The nearest player or empty optional, if no players are matched.
    */
    private static @NotNull Optional<Player> getNearestPlayer(@NotNull Player player, @NotNull PlayerTrackerConfig config) {
        Stream<Player> players = player.getWorld().getPlayers()
            .stream()
            .filter(other -> other != player);
        
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
            other.getActivePotionEffects().stream()
                .noneMatch(e -> e.getType().equals(PotionEffectType.INVISIBILITY))
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
        
        return players.min(comparing(
            candidate -> candidate.getLocation().distanceSquared(player.getLocation())));
    }

    /**
     * Creates a custom label for a provided player to display for them in some way.
     * @param player The player who is going to get the label updated.
     * @param config Plugin configuration.
     * @return A message to display.
     */
    public static @NotNull String createCompassMessage(@NotNull Player player, @NotNull PlayerTrackerConfig config) {
        String trackerText = config.getMessages().getTemplateMissing();
        final Optional<Player> nearestPlayer = getNearestPlayer(player, config);

        if (nearestPlayer.isPresent()) {
            Location nearestLocation = nearestPlayer.get().getLocation();
            player.setCompassTarget(nearestLocation);
            double distance = nearestLocation.distance(player.getLocation());
            trackerText = config.getMessages().getTemplateFound()
                .replace("{{name}}", nearestPlayer.get().getPlayerListName())
                .replace("{{distance}}", String.format("%.1f", distance))
                .replace("{{distanceft}}", String.format("%.1f", distance * FEET_IN_METER));
        }

        return translateAlternateColorCodes('%', trackerText);
    }

    /**
    * Updates the player's compass target
    * and shows them an action bar with details.
    * @param player The player who will have their compass updated.
    */
    public static void updateCompass(@NotNull Player player, @NotNull PlayerTrackerConfig config) {
        // If player is offline or doesn't have a compass, do nothing
        if (!player.isOnline() || !player.getInventory().contains(COMPASS)) {
            return;
        }
        
        final String messageText = createCompassMessage(player, config);
        final BaseComponent[] messageComponent = TextComponent.fromLegacyText(messageText);
        PlayerInventory inventory = player.getInventory();

        switch (config.getDisplayMethod()) {
            case ACTION_BAR:
                Material mainHand = inventory.getItemInMainHand().getType();
                Material offHand = inventory.getItemInOffHand().getType();
                if (COMPASS.equals(mainHand) || COMPASS.equals(offHand)) {
                    player.spigot().sendMessage(ACTION_BAR, messageComponent);
                }
                break;
            case ITEM_NAME:
                Arrays.stream(inventory.getContents())
                    .filter(item -> item != null && COMPASS.equals(item.getType()))
                    .forEach(item -> {
                        ItemMeta meta = item.getItemMeta();
                        meta.setDisplayName(messageText);
                        item.setItemMeta(meta);
                    });
                break;
        }
    }
}