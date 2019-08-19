package me.frixu.playertracker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerTracker extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Frixu's PlayerTracker is enabled!");
    }


    @Override
    public void onDisable() {
        getLogger().info("Frixu's PlayerTracker is disabled!");
    }
}