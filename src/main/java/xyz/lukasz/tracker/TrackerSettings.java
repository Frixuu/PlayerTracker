package xyz.lukasz.tracker;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class TrackerSettings {

    TrackerMode mode;
    UUID currentTarget;

    public static TrackerSettings ofNearestMode() {
        return new TrackerSettings(TrackerMode.NEAREST_PLAYER, null);
    }

    public static TrackerSettings ofTargetMode(Player player) {
        return new TrackerSettings(TrackerMode.TARGET_PLAYER, player.getUniqueId());
    }
}
