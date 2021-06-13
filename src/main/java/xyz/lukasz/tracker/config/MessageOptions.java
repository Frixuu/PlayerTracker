package xyz.lukasz.tracker.config;

import lombok.Data;

@Data
public class MessageOptions {
    public String overridden;
    public String nearestFound;
    public String nearestMissing;
    public String targetFound;
    public String targetMissing;
}