package com.townprotection;
import com.townprotection.CommandRun.MainCommand;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Listener.BlockBreakListener;
import com.townprotection.Listener.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import static com.townprotection.Data.MainData.markData;
import static com.townprotection.Useful.*;

public final class TownProtection extends JavaPlugin {

    public static final String message = toColor("&a&l" + "[TownProtection]&f&l");
    public static TownProtection instance = null;

    @Override
    public void onEnable() {
        instance = this;
        var command = getCommand("townprotection");
        command.setExecutor(new MainCommand());

        getServer().getPluginManager().registerEvents(new Listener(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }

    @Override
    public void onDisable() {
    }
}
