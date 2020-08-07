package io.github.frixuu.playertracker.utils;

import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

public final class LocationUtils {
    /**
     * Gets the player the closest to another player.
     * @param player Player to query.
     * @return The nearest player or empty optional, if no players are matched.
     */
    public static @NotNull Optional<Player> getNearestPlayer(@NotNull Player player, @NotNull PlayerTrackerConfig config) {
        Stream<Player> players = player.getWorld().getPlayers()
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
            players = players.filter(other -> {
                Team playerTeam = player.getScoreboard().getEntryTeam(player.getName());
                if (playerTeam == null) return true;
                return !playerTeam.hasEntry(other.getName());
            });
        }

        if (!config.getTracker().isTrackingSameColor()) {
            String myColor = player.getPlayerListName().replace(player.getName(), "");
            players = players.filter(other -> {
                String otherColor = other.getPlayerListName().replace(other.getName(), "");
                return !myColor.equals(otherColor);
            });
        }

        if (!config.getTracker().isTrackingOtherColors()) {
            String myColor = player.getPlayerListName().replace(player.getName(), "");
            players = players.filter(other -> {
                String otherColor = other.getPlayerListName().replace(other.getName(), "");
                return myColor.equals(otherColor);
            });
        }

        return players.min(comparing(
            candidate -> candidate.getLocation().distanceSquared(player.getLocation())));
    }
}
