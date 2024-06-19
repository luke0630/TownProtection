package com.townprotection.System;


import com.townprotection.Data.MainData;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.entity.Player;

import static com.townprotection.Data.MainData.playerOpenGUI;

public class CallBackStringByChat {
    public static void SetName(Player player, String message, RunnableSystem.Runnable runnable) {
        player.sendMessage(Useful.toColor(message));
        var previousData = playerOpenGUI.get(player).clone();
        player.closeInventory();
        RunnableSystem.Runnable callback = (Object s) -> {
            if(s instanceof String result) {
                if(result.length() > 20) {
                    player.sendMessage(message + Useful.toColor("&c&l名前は20文字以内でないといけません!" + "&a&l(" + result.length() + "文字)" + " &6&lもう一度名前変更ボタンを押して再度設定しなおしてください。"));
                } else {
                    runnable.run(result);
                    playerOpenGUI.put(player, previousData);
                    TownProtection.Save();
                    //GuiManager.openGUI(player ,previousData.gui);
                }
            }
        };
        MainData.setNameRunnable.put(player, callback);
    }
}
