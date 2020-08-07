package io.github.frixuu.playertracker.utils;

import io.github.frixuu.playertracker.config.PlayerTrackerConfig;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

import static io.github.frixuu.playertracker.utils.LocationUtils.getNearestPlayer;
import static net.md_5.bungee.api.ChatMessageType.ACTION_BAR;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
* Aggregates multiple functions useful in
* calculating player distance or updating inventories.
*/
public final class CompassUtils {
    private static final double FEET_IN_METER = 3.28084;
    private static final Material COMPASS = Material.getMaterial("COMPASS");

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