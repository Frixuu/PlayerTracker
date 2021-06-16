package xyz.lukasz.tracker.util;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.lukasz.tracker.TrackerSettings;
import xyz.lukasz.tracker.config.FilterOptions;
import xyz.lukasz.tracker.config.PluginConfig;

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
                                                       @NotNull PluginConfig config,
                                                       @NotNull TrackerSettings settings) {

        Player target = null;
        String trackerText = "???";

        switch (settings.getMode()) {
            case NEAREST_PLAYER:
                final var nearestPlayer = getNearestPlayer(player, config);
                if (nearestPlayer.isPresent()) {
                    target = nearestPlayer.get();
                    trackerText = config.getMessages().getNearestFound();
                } else {
                    trackerText = config.getMessages().getNearestMissing();
                }
                break;
            case TARGET_PLAYER:
                final var server = player.getServer();
                final var targetId = settings.getCurrentTarget();
                target = server.getPlayer(targetId);

                if (target == null) {
                    final var newTarget = chooseNext(
                        player.getWorld().getPlayers(),
                        player,
                        null,
                        config.getFilters());
                    if (newTarget.isEmpty()) {
                        trackerText = config.getMessages().getTargetMissing();
                        break;
                    } else {
                        target = newTarget.get();
                        trackerText = config.getMessages().getTargetFound();
                    }
                } else {
                    trackerText = config.getMessages().getTargetFound();
                }

                break;
        }

        if (target != null) {
            trackerText = trackerText.replace("{{name}}", target.getName());
            if (player.getWorld().equals(target.getWorld())) {
                final var targetLocation = target.getLocation();
                player.setCompassTarget(targetLocation);
                final var distance = targetLocation.distance(player.getLocation());
                trackerText = trackerText
                    .replace("{{distance}}", String.format("%.1f", distance))
                    .replace("{{distanceft}}", String.format("%.1f", distance * FEET_IN_METER));
            } else {
                trackerText = trackerText
                    .replace("{{distance}}", "???")
                    .replace("{{distanceft}}", "???");
            }
        }

        return translateAlternateColorCodes('&', trackerText);
    }

    public static Optional<? extends Player> chooseNext(
        @NotNull Collection<? extends Player> candidates,
        @NotNull Player owner,
        @Nullable String oldName,
        FilterOptions filters) {

        if (oldName == null) {
            return candidates.stream()
                .filter(c -> Filters.canBeTracked(owner, c, filters))
                .findAny();
        } else {
            final var sorted = candidates
                .stream()
                .filter(c -> Filters.canBeTracked(owner, c, filters))
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
                              @NotNull PluginConfig config,
                              @NotNull TrackerSettings settings) {

        // If player is offline or doesn't have a compass, do nothing
        if (!player.isOnline() || !player.getInventory().contains(COMPASS)) {
            return;
        }
        
        final var messageText = createCompassMessage(player, config, settings);
        final var messageComponent = TextComponent.fromLegacyText(messageText);
        final var inventory = player.getInventory();

        switch (config.getTechnical().getDisplayMethod()) {
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