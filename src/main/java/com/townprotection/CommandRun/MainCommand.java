package com.townprotection.CommandRun;

import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Selector.GiveSelector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.townprotection.Data.MainData.markData;
import static com.townprotection.Data.MainData.playerSelectData;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase("townprotection")) return false;
        if(commandSender instanceof Player player) {

            if(strings[0].equalsIgnoreCase("wand")) {
                GiveSelector.giveSelector(player);
            } else if(strings[0].equals("mark")) {
                if(playerSelectData.containsKey(player) && playerSelectData.get(player).endBlock != null && playerSelectData.get(player).startBlock != null ) {

                    var selectMarkData = new SelectorMarkData();
                    selectMarkData.selectorData = playerSelectData.get(player).clone(); //ディープコピーを作成
                    markData.put(markData.size(), selectMarkData);
                    return false;
                }
            }
            //******DEBUG******//
            else if(strings[0].equalsIgnoreCase("test")) {
                markData.get(0).allowActionList.add(ActionList.Action.PLAYER_BREAK_BLOCK);
            }

        } else {
            commandSender.sendMessage("プレイヤー以外実行できません");
        }
        return false;
    }
}
