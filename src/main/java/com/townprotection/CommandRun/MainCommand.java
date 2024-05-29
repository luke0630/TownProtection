package com.townprotection.CommandRun;

import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Selector.GiveSelector;
import com.townprotection.Selector.Selector;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.townprotection.TownProtection.*;
import static com.townprotection.Data.MainData.*;
import static com.townprotection.Useful.*;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(command.getName().equalsIgnoreCase("townprotection"))
        if(commandSender instanceof Player player) {

            if(strings[0].equalsIgnoreCase("wand")) {
                GiveSelector.giveSelector(player);
            } else if(strings[0].equalsIgnoreCase("mark")) {
                if(playerSelectData.containsKey(player) && playerSelectData.get(player).endBlock != null && playerSelectData.get(player).startBlock != null ) {
                    var selectMarkData = new SelectorMarkData();
                    selectMarkData.selectorData = playerSelectData.get(player);
                    markData.put(markData.size(), selectMarkData);
                }
            }

        } else {
            commandSender.sendMessage("プレイヤー以外実行できません");
        }
        return false;
    }
}
