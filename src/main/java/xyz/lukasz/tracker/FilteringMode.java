package xyz.lukasz.tracker;

public enum FilteringMode {
    /** This filter will be skipped. */
    DEFAULT,
    /** The compass will choose from the players that match this filter. */
    REQUIRE,
    /** The compass will choose from the players that do not match this filter. */
    EXCLUDE,
}
