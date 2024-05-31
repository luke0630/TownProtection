package com.townprotection;

import com.townprotection.CommandRun.MainCommand;
import com.townprotection.Listener.BlockBreakListener;
import com.townprotection.Listener.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static com.townprotection.Useful.toColor;

public final class TownProtection extends JavaPlugin {

    public static final String message = toColor("&a&l" + "[TownProtection]&f&l");
    public static TownProtection instance = null;

    @Override
    public void onEnable() {
        instance = this;
        var command = getCommand("townprotection");
        Objects.requireNonNull(command).setExecutor(new MainCommand());

        getServer().getPluginManager().registerEvents(new Listener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
