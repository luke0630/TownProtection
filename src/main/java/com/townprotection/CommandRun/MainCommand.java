package com.townprotection.CommandRun;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MainData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Selector.GiveSelector;
import com.townprotection.Selector.Selector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.Selector.Selector.*;
import static com.townprotection.Useful.toColor;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!command.getName().equalsIgnoreCase("townprotection")) return false;
        if(commandSender instanceof Player player) {
            if(showModePlayer.contains(player)) {
                if(strings.length == 0) {
                    ShowMode(player);
                } else if(!strings[0].equalsIgnoreCase("show")) {
                    ShowMode(player);
                }
            }
            if(strings.length == 0) {
                playerOpenGUI.put(player, new GUIData());
                if(changeSelectorDataPlayer.containsKey(player)) {
                    GuiManager.openGUI(player, GuiManager.GUi.APPLY_SELECTOR_DATA);
                    return false;
                }
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_LIST);
                return false;
            }

            String targetPermission = "townprotection.commands." + strings[0];
            if(!player.hasPermission(targetPermission)) {
                player.sendMessage(toColor("&cあんたはこれを実行する権限を持っていません！"));
                return false;
            }

            switch (strings[0]) {
                case "show" -> Selector.ShowMode(player);
                case "wand" -> GiveSelector.giveSelector(player);
                case "deselect" -> {
                    if(!schedulers.isEmpty()) {
                        for (var scheduler : schedulers.get(player)) {
                            scheduler.cancel();
                        }
                        schedulers.remove(player);
                    }
                    if(!playerSelectData.containsKey(player)) {
                        player.sendMessage(toColor("&c選択範囲が存在しなかったため削除しませんでした。"));
                        return false;
                    }
                    playerSelectData.remove(player);
                    player.sendMessage(toColor("&a選択範囲を消しました。"));
                }
                case "open" -> {
                    if(strings.length == 1) {
                        player.sendMessage(toColor("&c第二引数が含まれていません。"));
                        return false;
                    }

                    for (var townData : MainData.townMarkData) {
                        if (townData.townName.equalsIgnoreCase(strings[1])) {
                            var guiData = new GUIData();
                            guiData.targetTownData = townData;
                            playerOpenGUI.put(player, guiData);
                            GuiManager.openGUI(player, GuiManager.GUi.TOWN_EDITOR);
                            return false;
                        }
                    }
                    player.sendMessage(toColor("&cその名前の町は存在しないため開けませんでした。"));
                    return false;
                }
            }

        } else {
            commandSender.sendMessage("プレイヤー以外実行できません");
        }
        return false;
    }
}

