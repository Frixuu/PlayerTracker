package xyz.lukasz.tracker.util;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.lukasz.tracker.TrackerSettings;
import xyz.lukasz.tracker.config.PlayerTrackerConfig;
import xyz.lukasz.tracker.config.TrackerOptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
import static org.bukkit.ChatColor.translateAlternateColorCodes;
import static xyz.lukasz.tracker.util.LocationUtils.getNearestPlayer;

/**
* Aggregates multiple functions useful in
* calculating player distance or updating inventories.
*/
public final class Compasses {

    private static final double FEET_IN_METER = 3.28084;
    private static final Material COMPASS = Material.COMPASS;

    /**
     * Creates a custom label for a provided player to display for them in some way.
     * @param player The player who is going to get the label updated.
     * @param config Plugin configuration.
     * @return A message to display.
     */
    public static @NotNull String createCompassMessage(@NotNull Player player,
                                                       @NotNull PlayerTrackerConfig config,
                                                       @NotNull TrackerSettings settings) {

        String trackerText = config.getMessages().getTemplateMissing();

        switch (settings.getMode()) {
            case NEAREST_PLAYER:
                final var nearestPlayer = getNearestPlayer(player, config);
                if (nearestPlayer.isPresent()) {
                    Location nearestLocation = nearestPlayer.get().getLocation();
                    player.setCompassTarget(nearestLocation);
                    double distance = nearestLocation.distance(player.getLocation());
                    trackerText = config.getMessages().getTemplateFound()
                        .replace("{{name}}", nearestPlayer.get().getPlayerListName())
                        .replace("{{distance}}", String.format("%.1f", distance))
                        .replace("{{distanceft}}", String.format("%.1f", distance * FEET_IN_METER));
                }
                break;
            case TARGET_PLAYER:
                final var server = player.getServer();
                var target = server.getPlayer(settings.getCurrentTarget());

                if (target == null) {
                    final var newTarget = chooseNext(
                        player.getWorld().getPlayers(),
                        player,
                        null,
                        config.getTracker());
                    if (newTarget.isEmpty()) {
                        trackerText = "%l%cBrak graczy!";
                        break;
                    } else {
                        target = newTarget.get();
                        player.setCompassTarget(target.getLocation());
                    }
                } else {
                    player.setCompassTarget(target.getLocation());
                }

                final var distance = target.getLocation().distance(player.getLocation());
                trackerText = "%r{{name}} - {{distance}}m"
                    .replace("{{name}}", target.getName())
                    .replace("{{distance}}", String.format("%.1f", distance))
                    .replace("{{distanceft}}", String.format("%.1f", distance * FEET_IN_METER));
                break;
        }

        return translateAlternateColorCodes('%', trackerText);
    }

    public static Optional<? extends Player> chooseNext(
        @NotNull Collection<? extends Player> candidates,
        @NotNull Player owner,
        @Nullable String oldName,
        TrackerOptions config) {

        if (oldName == null) {
            return candidates.stream()
                .filter(c -> Filters.canBeTracked(owner, c, config))
                .findAny();
        } else {
            final var sorted = candidates
                .stream()
                .filter(c -> Filters.canBeTracked(owner, c, config))
                .map(HumanEntity::getName)
                .filter(name -> !name.equals(owner.getName()))
                .sorted()
                .toArray(String[]::new);

            if (sorted.length < 2) {
                return Optional.empty();
            }

            final var newTargetName = sorted[sorted.length - 1].equals(oldName)
                ? sorted[0]
                : sorted[Arrays.binarySearch(sorted, oldName) + 1];

            return candidates.stream().filter(p -> p.getName().equals(newTargetName)).findAny();
        }
    }

    /**
    * Updates the player's compass target
    * and shows them an action bar with details.
    * @param player The player who will have their compass updated.
    */
    public static void update(@NotNull Player player,
                              @NotNull PlayerTrackerConfig config,
                              @NotNull TrackerSettings settings) {

        // If player is offline or doesn't have a compass, do nothing
        if (!player.isOnline() || !player.getInventory().contains(COMPASS)) {
            return;
        }
        
        final var messageText = createCompassMessage(player, config, settings);
        final var messageComponent = TextComponent.fromLegacyText(messageText);
        final var inventory = player.getInventory();

        switch (config.getDisplayMethod()) {
            case ACTION_BAR:
                final var mainHand = inventory.getItemInMainHand().getType();
                final var offHand = inventory.getItemInOffHand().getType();
                if (COMPASS.equals(mainHand) || COMPASS.equals(offHand)) {
                    player.spigot().sendMessage(ACTION_BAR, messageComponent);
                }
                break;
            case ITEM_NAME:
                Stream.of(inventory.getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> COMPASS.equals(item.getType()))
                    .forEach(item -> {
                        var meta = item.getItemMeta();
                        meta.setDisplayName(messageText);
                        item.setItemMeta(meta);
                    });
                break;
        }
    }
}