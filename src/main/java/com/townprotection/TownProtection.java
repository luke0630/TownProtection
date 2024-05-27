package com.townprotection;
import com.townprotection.CommandRun.MainCommand;
import org.bukkit.plugin.java.JavaPlugin;

import static com.townprotection.Useful.*;

public final class TownProtection extends JavaPlugin {

    public static final String message = toColor("&a&l") + "[TownProtection]&f&l";

    @Override
    public void onEnable() {
        var command = getCommand("townprotection");
        command.setExecutor(new MainCommand());
    }

    @Override
    public void onDisable() {
    }
}
