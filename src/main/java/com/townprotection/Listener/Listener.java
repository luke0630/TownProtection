package com.townprotection.Listener;

import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Selector.Selector;
import com.townprotection.System.RunnableSystem;
import com.townprotection.TownProtection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;

import static com.townprotection.Data.MainData.*;
import static com.townprotection.TownProtection.message;
import static com.townprotection.Useful.getXYZMessage;
import static com.townprotection.Useful.toColor;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onClickBlock(PlayerInteractEvent event) {
        var player = event.getPlayer();

        if (event.getHand() != EquipmentSlot.HAND) return; //二度呼ばれないようにする対策


        if(Selector.IsSelectorTool(player)) {
            player.sendMessage("あああんで");
            if (event.getClickedBlock() == null) return;
            if (!event.getClickedBlock().getType().isAir()) {
                var location = event.getClickedBlock().getLocation();
                var locData = playerSelectData.get(player);
                if (locData == null) {
                    locData = new SelectorData();
                }

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    //左クリックで開始地点
                    locData.startBlock = location;
                    player.sendMessage(message + toColor("&d&l開始地点を設定しました。&f&l" + getXYZMessage(location)));
                } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    //左クリックで終了地点
                    locData.endBlock = location;
                    player.sendMessage(message + toColor("&d&l終了地点を設定しました。&f&l" + getXYZMessage(location)));
                }

                playerSelectData.put(player, locData);
                if(locData.startBlock != null && locData.endBlock != null) {
                    if(Selector.schedulers.containsKey(player)) {
                        for(var schedule : Selector.schedulers.get(player)) {
                            schedule.cancel();
                        }
                    }
                    //パーティクルを表示する
                    Selector.getRange(player, locData, Color.AQUA);
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        var player = event.getPlayer();
        RunnableSystem.Runnable data = setNameRunnable.get(player);
        if(data != null) {
            setNameRunnable.remove(player);
            Bukkit.getScheduler().runTask(TownProtection.instance, () -> data.run(event.getMessage()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent event) {
        var player = (Player) event.getPlayer();
        playerOpenGUI.remove(player);
    }

    @EventHandler
    public void onQuitPlayer(PlayerQuitEvent event) {
        var player = event.getPlayer();
        playerOpenGUI.remove(player);
    }
}
