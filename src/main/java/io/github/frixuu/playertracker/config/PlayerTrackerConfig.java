package io.github.frixuu.playertracker.config;

import lombok.Data;

@Data
public class PlayerTrackerConfig {
    public static final String FILENAME = "config.json";
    public TrackerOptions tracker;
    public MessageOptions messages;
    public long updateTickInterval;
    public DisplayMethod displayMethod;
}