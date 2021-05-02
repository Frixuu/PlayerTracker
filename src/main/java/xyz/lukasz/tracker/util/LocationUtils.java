package xyz.lukasz.tracker.util;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.lukasz.tracker.config.PlayerTrackerConfig;

import java.util.Comparator;
import java.util.Optional;

public final class LocationUtils {

    /**
     * Gets the player the closest to another player.
     * @param player Player to query.
     * @return The nearest player or empty optional, if no players are matched.
     */
    public static @NotNull Optional<Player> getNearestPlayer(@NotNull Player player,
                                                             @NotNull PlayerTrackerConfig config) {

        final var playerLoc = player.getLocation();
        return player.getWorld()
            .getPlayers()
            .stream()
            .filter(c -> Filters.canBeTracked(player, c, config.getTracker()))
            .min(Comparator.comparing(c -> c.getLocation().distanceSquared(playerLoc)));
    }
}
