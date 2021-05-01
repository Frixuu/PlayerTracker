package xyz.lukasz.tracker;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.google.common.cache.CacheLoader.from;

public class ModeManager {

    @Getter @Setter
    private @Nullable TrackerSettings globalOverride;

    /**
     * Contains current mode information about players' compasses.
     */
    @Getter
    private final LoadingCache<UUID, TrackerSettings> modeMapping = CacheBuilder.newBuilder()
        .build(from(uuid -> TrackerSettings.ofNearestMode()));

    public TrackerSettings getPlayerSettings(Player player) {
        return globalOverride != null
            ? globalOverride
            : getModeMapping().getUnchecked(player.getUniqueId());
    }
}
