package com.townprotection.Listener;

import com.townprotection.Data.MainData;
import com.townprotection.Selector.Selector;
import com.townprotection.System.LocationRunnableSystem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class BlockBreakListener implements Listener {

    void Checker(Player player, Location blockLoc, Runnable callback) {
        LocationRunnableSystem.LocationRunnable callBack = (Location loc) -> {
            if(player != null) {
                if(loc.getWorld() != player.getWorld()) return;
            }
            if(loc.getBlockX() == blockLoc.getBlockX() && loc.getBlockZ() == blockLoc.getBlockZ()) {
                callback.run();
            }
        };

        for(var data : MainData.markData.values()) {
            Selector.getRange(player, data.selectorData, callBack);
        }
    }

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        var player = event.getPlayer();
        var blockLoc = event.getBlock().getLocation();

        Checker(player,blockLoc, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        var player = event.getPlayer();
        var blockLoc = event.getBlock().getLocation();

        Checker(player,blockLoc, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        var damager = event.getDamager(); //与えた人
        var target = event.getEntity().getLocation(); //ダメージを与えられた人

        if(damager instanceof Player player) {
            Checker(player, target, () -> event.setCancelled(true));
        }
    }


    //額縁など↓
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
        var damager = event.getRemover(); //プレイヤー
        var target = event.getEntity().getLocation(); //その場所

        if(damager instanceof Player player) {
            Checker(player, target, () -> event.setCancelled(true));
        }
    }
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakEvent event) {
        var target = event.getEntity();
        var cause = event.getCause();
        if (cause == HangingBreakEvent.RemoveCause.EXPLOSION) {
            Checker(null, target.getLocation(), () -> event.setCancelled(true));
        }
    }



    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if(event.getClickedBlock() == null) return;
        var blockLoc = event.getClickedBlock().getLocation();
        Checker(player,blockLoc, () -> event.setCancelled(true));
    }

    //TNTなど
    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        var loc = event.getEntity().getLocation();
        Checker(null, loc, () -> event.setCancelled(true));
    }


    //↓トロッコやボートなどが対象
    @EventHandler
    public void onVehicleDamageEvent(VehicleDamageEvent event) {
        var loc = event.getVehicle().getLocation();
        Checker(null, loc, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onVehicleEnterEvent(VehicleEnterEvent event) {
        var loc = event.getVehicle().getLocation();
        Checker(null, loc, () -> event.setCancelled(true));
    }
}
