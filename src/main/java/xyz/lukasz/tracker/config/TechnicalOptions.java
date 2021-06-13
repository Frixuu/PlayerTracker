package xyz.lukasz.tracker.config;

import lombok.Data;

@Data
public class TechnicalOptions {
    public long updateInterval;
    public DisplayMethod displayMethod = DisplayMethod.ITEM_NAME;
    public boolean bStats;
}
