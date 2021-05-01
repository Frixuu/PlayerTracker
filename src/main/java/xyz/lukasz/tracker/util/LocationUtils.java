package xyz.lukasz.tracker.util;

import xyz.lukasz.tracker.config.PlayerTrackerConfig;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

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

        var players = player.getWorld().getPlayers()
            .stream()
            .filter(other -> other != player);

        if (!config.getTracker().isTrackingSpectators()) {
            players = players.filter(other ->
                !other.getGameMode().equals(GameMode.SPECTATOR)
            );
        }

        if (!config.getTracker().isTrackingHidden()) {
            players = players.filter(other ->
                !player.spigot().getHiddenPlayers().contains(other)
            );
        }

        if (!config.getTracker().isTrackingInvisible()) {
            players = players.filter(other ->
                other.getActivePotionEffects().stream()
                    .noneMatch(e -> e.getType().equals(PotionEffectType.INVISIBILITY))
            );
        }

        if (!config.getTracker().isTrackingTeamScoreboard()) {
            final var playerTeam = player.getScoreboard().getEntryTeam(player.getName());
            if (playerTeam != null) {
                players = players.filter(other -> !playerTeam.hasEntry(other.getName()));
            }
        }

        if (!config.getTracker().isTrackingOtherTeams()) {
            final var playerTeam = player.getScoreboard().getEntryTeam(player.getName());
            if (playerTeam != null) {
                players = players.filter(other -> playerTeam.hasEntry(other.getName()));
            }
        }

        if (!config.getTracker().isTrackingSameColor()) {
            final var myColor = player.getPlayerListName().replace(player.getName(), "");
            players = players.filter(other -> {
                final var otherColor = other.getPlayerListName().replace(other.getName(), "");
                return !myColor.equals(otherColor);
            });
        }

        if (!config.getTracker().isTrackingOtherColors()) {
            final var myColor = player.getPlayerListName().replace(player.getName(), "");
            players = players.filter(other -> {
                final var otherColor = other.getPlayerListName().replace(other.getName(), "");
                return myColor.equals(otherColor);
            });
        }

        return players.min(Comparator.comparing(
            candidate -> candidate.getLocation().distanceSquared(player.getLocation())));
    }
}
