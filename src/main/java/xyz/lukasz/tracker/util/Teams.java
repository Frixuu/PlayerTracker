package xyz.lukasz.tracker.util;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class Teams {

    public static boolean areInSameTeam(@NotNull Player p, @NotNull Player q) {
        final var pTeam = p.getScoreboard().getEntryTeam(p.getName());
        final var qTeam = q.getScoreboard().getEntryTeam(q.getName());
        return (pTeam == null && qTeam == null) || (pTeam != null && pTeam.hasEntry(q.getName()));
    }

    public static boolean areInDifferentTeams(@NotNull Player p, @NotNull Player q) {
        return !areInSameTeam(p, q);
    }

    public static boolean haveSameColor(@NotNull Player p, @NotNull Player q) {
        final var pColor = p.getPlayerListName().replace(p.getName(), "");
        final var qColor = q.getPlayerListName().replace(q.getName(), "");
        return pColor.equals(qColor);
    }

    public static boolean haveDifferentColors(@NotNull Player p, @NotNull Player q) {
        return !haveSameColor(p, q);
    }
}
