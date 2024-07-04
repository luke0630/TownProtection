package com.townprotection.Listener;

import com.townprotection.Selector.Selector;
import com.townprotection.System.RunnableSystem;
import com.townprotection.TownProtection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Data.MainData.setNameRunnable;
import static com.townprotection.Range.ShowRange.PlaceFakeBlock;
import static com.townprotection.Selector.Selector.changeSelectorDataPlayer;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onClickBlock(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if(!changeSelectorDataPlayer.containsKey(player)) return;
        if(Selector.IsSelectorTool(player)  && event.getClickedBlock() != null) {
            Bukkit.getScheduler().runTaskAsynchronously(TownProtection.instance, () -> {
                PlaceFakeBlock(player, event.getClickedBlock().getLocation(), Material.GOLD_BLOCK);
            });
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
