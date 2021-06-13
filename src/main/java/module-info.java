module xyz.lukasz.playertracker {

    requires bstats.bukkit.lite;
    requires bungeecord.chat;
    requires guava;
    requires java.logging;
    requires lombok;
    requires org.bukkit;
    requires org.jetbrains.annotations;
    requires snakeyaml;

    exports xyz.lukasz.tracker;
    exports xyz.lukasz.tracker.command;
    exports xyz.lukasz.tracker.config;
    exports xyz.lukasz.tracker.event;
    exports xyz.lukasz.tracker.util;

    opens xyz.lukasz.tracker.config;
}