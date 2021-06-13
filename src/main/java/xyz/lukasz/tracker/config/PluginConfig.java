package xyz.lukasz.tracker.config;

import lombok.Data;

@Data
public class PluginConfig {
    public FilterOptions filters;
    public MessageOptions messages;
    public TechnicalOptions technical;
}