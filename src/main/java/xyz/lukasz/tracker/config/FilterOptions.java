package xyz.lukasz.tracker.config;

import lombok.Data;
import xyz.lukasz.tracker.FilteringMode;

@Data
public class FilterOptions {
    public FilteringMode hidden = FilteringMode.DEFAULT;
    public FilteringMode invisible = FilteringMode.DEFAULT;
    public FilteringMode spectators = FilteringMode.DEFAULT;
    public FilteringMode sameTeam = FilteringMode.DEFAULT;
    public FilteringMode sameColor = FilteringMode.DEFAULT;
}