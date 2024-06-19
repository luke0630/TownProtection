package com.townprotection.CommandRun;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Selector.GiveSelector;
import com.townprotection.Selector.Selector;
import com.townprotection.System.SaveLoad;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.townprotection.Data.MainData.*;
import static com.townprotection.TownProtection.message;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase("townprotection")) return false;
        if(commandSender instanceof Player player) {

            if(strings.length == 0) {
                playerOpenGUI.put(player, new GUIData());
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_LIST);
                return false;
            }
            if(strings.length == 1) {
                switch(strings[0]) {
                }
            } else if(strings.length == 2) {
                if(strings[0].equalsIgnoreCase("open")) {
                    for(var town : townMarkData) {
                        if(strings[1].equalsIgnoreCase(town.townName)) {
                            if(!playerOpenGUI.containsKey(player)) {
                                playerOpenGUI.put(player, new GUIData());
                            }
                            playerOpenGUI.get(player).targetTownData = town;
                            GuiManager.openGUI(player, GuiManager.GUi.TOWN_EDITOR);
                            break;
                        }
                    }
                    player.sendMessage(message + strings[1] + " という町は存在しないため開けませんでした。");
                }
            }
            if(strings[0].equalsIgnoreCase("wand")) {
                GiveSelector.giveSelector(player);
            }
            else if(strings[0].equals("save")) {
                new SaveLoad().SaveToConfig();
            }
            //******DEBUG******//
            else if(strings[0].equalsIgnoreCase("test")) {
                markData.get(0).allowActionList.add(ActionList.Action.PLAYER_BREAK_BLOCK);
            }
            else if(strings[0].equalsIgnoreCase("deselect")) {
                for (var scheduler : Selector.schedulers.get(player)) {
                    scheduler.cancel();
                }
                playerSelectData.remove(player);
            }

        } else {
            commandSender.sendMessage("プレイヤー以外実行できません");
        }
        return false;
    }
}
