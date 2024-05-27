package com.townprotection.CommandRun;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("townprotection"))
        if(commandSender instanceof Player) {
            var player = (Player) commandSender;

            if(strings[0].equalsIgnoreCase("wand")) {
                player.getInventory().addItem( getItem(Material.WOODEN_PICKAXE, "ああああ") );
            }

        } else {
            commandSender.sendMessage("");
        }
        return false;
    }
}
