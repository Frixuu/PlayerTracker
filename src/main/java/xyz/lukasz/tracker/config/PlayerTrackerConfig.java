package xyz.lukasz.tracker.config;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class PlayerTrackerConfig {
    public static final String FILENAME = "config.json";
    public TrackerOptions tracker;
    public MessageOptions messages;
    public long updateTickInterval;
    public DisplayMethod displayMethod;
    @SerializedName("bStats")
    public boolean telemetryActive = true;
}