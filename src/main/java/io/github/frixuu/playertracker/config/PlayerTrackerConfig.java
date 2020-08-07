package io.github.frixuu.playertracker.config;

import lombok.Data;

@Data
public class PlayerTrackerConfig {
    public TrackerOptions tracker;
    public MessageOptions messages;
    public long updateTickInterval;
    public DisplayMethod displayMethod;
}