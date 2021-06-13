package xyz.lukasz.tracker.util;

import lombok.experimental.UtilityClass;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;

@UtilityClass
public final class Runnables {

    public static BukkitRunnable bukkitRunnable(Runnable r) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                r.run();
            }
        };
    }
}
