package xyz.lukasz.tracker.event;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.lukasz.tracker.ModeManager;
import xyz.lukasz.tracker.TrackerMode;
import xyz.lukasz.tracker.config.PlayerTrackerConfig;
import xyz.lukasz.tracker.util.Compasses;

import java.util.logging.Logger;

@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {

    private final PlayerTrackerConfig config;
    private final ModeManager modeManager;
    private final Server server;
    private final Logger logger;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }

        final var item = event.getItem();
        if (item == null || !item.getType().equals(Material.COMPASS)) {
            return;
        }

        final var player = event.getPlayer();
        final var isOverrideActive = modeManager.getGlobalOverride() != null;
        if (isOverrideActive) {
            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&',
                        "&cAdministrator zablokował zmianę trybu kompasu.")));
            return;
        }

        final var currentSettings = modeManager.getModeMapping()
            .getUnchecked(event.getPlayer().getUniqueId());

        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if (currentSettings.getMode() == TrackerMode.TARGET_PLAYER) {
                    final var uuid = currentSettings.getCurrentTarget();
                    final var target = server.getPlayer(uuid);
                    if (uuid == null || target == null) {
                        currentSettings.setCurrentTarget(
                            server.getOnlinePlayers()
                                .stream()
                                .filter(t -> t != player)
                                .findAny()
                                .map(Entity::getUniqueId)
                                .orElse(null)
                        );
                    } else {
                        final var newTarget = Compasses.chooseNext(
                            player.getWorld().getPlayers(),
                            player,
                            target.getName(),
                            config.getTracker());

                        if (newTarget.isPresent()) {
                            currentSettings.setCurrentTarget(newTarget.get().getUniqueId());
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText("Śledzisz teraz " + newTarget.get().getName() + "!"));
                        } else {
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacyText("Nie można wybrać innej osoby!"));
                        }
                    }
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText("Musisz zmienić tryb kompasu, żeby to zrobić!"));
                }
                break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                if (currentSettings.getMode() == TrackerMode.TARGET_PLAYER) {
                    currentSettings.setMode(TrackerMode.NEAREST_PLAYER);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText("Twoje kompasy śledzą teraz najbliższą osobę!"));
                } else {
                    currentSettings.setMode(TrackerMode.TARGET_PLAYER);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacyText("Twoje kompasy śledzą teraz konkretną osobę!"));
                }
                break;
            default:
                break;
        }
    }
}
