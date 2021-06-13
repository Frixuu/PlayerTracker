package xyz.lukasz.tracker.util;

import lombok.experimental.UtilityClass;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.lukasz.tracker.config.FilterOptions;

import java.util.stream.Stream;

import static org.bukkit.potion.PotionEffectType.INVISIBILITY;
import static xyz.lukasz.tracker.FilteringMode.EXCLUDE;
import static xyz.lukasz.tracker.FilteringMode.REQUIRE;

@UtilityClass
public final class Filters {

    public static boolean canBeTracked(@NotNull Player compassOwner,
                                       @NotNull Player potentialTarget,
                                       @NotNull FilterOptions filters) {

        if (potentialTarget.equals(compassOwner)) {
            return false;
        }

        return Stream.of(
            Pair.of(filters.getHidden(), isHiddenFrom(potentialTarget, compassOwner)),
            Pair.of(filters.getInvisible(), hasInvisibilityEffect(potentialTarget)),
            Pair.of(filters.getSpectators(), isSpectator(potentialTarget)),
            Pair.of(filters.getSameTeam(), Teams.areInSameTeam(compassOwner, potentialTarget)),
            Pair.of(filters.getSameColor(), Teams.haveSameColor(compassOwner, potentialTarget))
        ).noneMatch(pair ->
            (pair.getLeft() == REQUIRE && !pair.getRight()) || (pair.getLeft() == EXCLUDE && pair.getRight())
        );
    }

    public static boolean isHiddenFrom(@NotNull Player target, @NotNull Player observator) {
        return observator.spigot().getHiddenPlayers().contains(target);
    }

    public static boolean hasInvisibilityEffect(Player player) {
        return player.getActivePotionEffects()
            .stream()
            .anyMatch(eff -> eff.getType().equals(INVISIBILITY));
    }

    public static boolean isSpectator(Player player) {
        return player.getGameMode().equals(GameMode.SPECTATOR);
    }
}
