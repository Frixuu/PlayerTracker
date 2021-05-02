package xyz.lukasz.tracker.util;

import lombok.experimental.UtilityClass;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.lukasz.tracker.config.TrackerOptions;

import static org.bukkit.potion.PotionEffectType.INVISIBILITY;
import static xyz.lukasz.tracker.util.Teams.areInDifferentTeams;
import static xyz.lukasz.tracker.util.Teams.areInSameTeam;
import static xyz.lukasz.tracker.util.Teams.haveDifferentColors;
import static xyz.lukasz.tracker.util.Teams.haveSameColor;

@UtilityClass
public final class Filters {

    public static boolean shouldBeExcluded(@NotNull Player compassOwner,
                                           @NotNull Player potentialTarget,
                                           @NotNull TrackerOptions config) {

        return (compassOwner == potentialTarget)
            || (config.shouldExcludeSpectators() && isSpectator(potentialTarget))
            || (config.shouldExcludeHidden() && compassOwner.spigot().getHiddenPlayers().contains(potentialTarget))
            || (config.shouldExcludeInvisible() && hasInvisibilityEffect(potentialTarget))
            || (config.shouldExcludeSameTeam() && areInSameTeam(compassOwner, potentialTarget))
            || (config.shouldExcludeOtherTeams() && areInDifferentTeams(compassOwner, potentialTarget))
            || (config.shouldExcludeSameColor() && haveSameColor(compassOwner, potentialTarget))
            || (config.shouldExcludeOtherColors() && haveDifferentColors(compassOwner, potentialTarget));
    }

    public static boolean canBeTracked(@NotNull Player compassOwner,
                                       @NotNull Player potentialTarget,
                                       @NotNull TrackerOptions config) {

        return !shouldBeExcluded(compassOwner, potentialTarget, config);
    }

    public static boolean isSpectator(Player player) {
        return player.getGameMode().equals(GameMode.SPECTATOR);
    }

    public static boolean isNotSpectator(Player player) {
        return !isSpectator(player);
    }

    public static boolean hasInvisibilityEffect(Player player) {
        return player.getActivePotionEffects()
            .stream()
            .anyMatch(eff -> eff.getType().equals(INVISIBILITY));
    }

    public static boolean doesNotHaveInvisibilityEffect(Player player) {
        return !hasInvisibilityEffect(player);
    }
}
