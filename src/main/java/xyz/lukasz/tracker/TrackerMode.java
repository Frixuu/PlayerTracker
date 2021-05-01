package xyz.lukasz.tracker;

/**
 * Specifies the way the compass works.
 */
public enum TrackerMode {

    /**
     * In this mode the compass shows the nearest player to you.
     * The exclusions of who may be shown are defined in the config.
     */
    NEAREST_PLAYER,

    /**
     * In this mode the compass only tracks the one person it was told to.
     * The players might or might not be allowed to change the compass' target.
     */
    TARGET_PLAYER,
}
