package com.townprotection.Listener;

import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Selector.GiveSelector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;

public class BlockBreakListener implements Listener {

    void Checker(Location blockLoc, ActionList.Action action, Runnable callback) {
        for(var data : MainData.markData.entrySet()) {
            if(data.getValue().allowActionList.contains(action)) continue; //許可されているアクションだったらreturnしてcancelさせない;
            var x = blockLoc.getX();
            var z = blockLoc.getZ();
            var selectData = data.getValue().selectorData;

            int startX = selectData.startBlock.getBlockX();
            int endX = selectData.endBlock.getBlockX();
            int minX = Math.min(startX, endX);
            int maxX = Math.max(startX, endX);

            int startZ = selectData.startBlock.getBlockZ();
            int endZ = selectData.endBlock.getBlockZ();
            int minZ = Math.min(startZ, endZ);
            int maxZ = Math.max(startZ, endZ);
            if (x >= minX && x <= maxX && z >= minZ && z <= maxZ) {
                // ここに実行する処理を書く
                //player.sendMessage("範囲内キチャー");
                callback.run();
            } else {
                //player.sendMessage("範囲外ですよん");
            }
        }
    }

    final ActionList.Action BREAK_BLOCK = ActionList.Action.PLAYER_BREAK_BLOCK;
    final ActionList.Action PLACE_BLOCK = ActionList.Action.PLAYER_PLACE_BLOCK;
    final ActionList.Action PLAYER_INTERACT = ActionList.Action.PLAYER_INTERACT;
    final ActionList.Action DAMAGE_ENTITY_BY_ENTITY = ActionList.Action.PLAYER_DAMAGE_TO_ENTITY;
    final ActionList.Action HANGING_BREAK = ActionList.Action.HANGING_BREAK;
    final ActionList.Action ENTITY_EXPLODE = ActionList.Action.TNT_EXPLOSION;
    final ActionList.Action VEHICLE_DAMAGE = ActionList.Action.VEHICLE_DAMAGE;
    final ActionList.Action VEHICLE_ENTER = ActionList.Action.VEHICLE_ENTER;
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        var player = event.getPlayer();
        var blockLoc = event.getBlock().getLocation();

        Checker(blockLoc, PLACE_BLOCK, () -> event.setCancelled(true));
    }
    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        var damager = event.getDamager(); //与えた人
        var target = event.getEntity().getLocation(); //ダメージを与えられた人

        if(damager instanceof Player player) {
            Checker(target, DAMAGE_ENTITY_BY_ENTITY,  () -> event.setCancelled(true));
        }
    }
    //額縁など↓
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
        var remover = event.getRemover(); //プレイヤー
        var target = event.getEntity().getLocation(); //その場所

        if(remover instanceof Player player) {
            Checker(target, HANGING_BREAK, () -> event.setCancelled(true));
        }
    }
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakEvent event) {
        var target = event.getEntity();
        var cause = event.getCause();
        if (cause == HangingBreakEvent.RemoveCause.EXPLOSION) {
            Checker(target.getLocation(), HANGING_BREAK, () -> event.setCancelled(true));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var action = event.getAction();
        if(event.getClickedBlock() == null) return;
        if(player.getInventory().getItemInMainHand().getType() == Material.WOODEN_PICKAXE) {
            if (player.getInventory().getItemInMainHand().getLore().get(0).equalsIgnoreCase(GiveSelector.SELECTOR_LORE)) return;
        }
        if(action == LEFT_CLICK_BLOCK) {
            var blockLoc = event.getClickedBlock().getLocation();
            Checker(blockLoc, BREAK_BLOCK, () -> event.setCancelled(true));
        }
        else
        {
            var blockLoc = event.getClickedBlock().getLocation();
            Checker(blockLoc, PLAYER_INTERACT, () -> event.setCancelled(true));
        }
    }
    //TNTなど
    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        var locList = event.blockList();
        for(var loc : locList) {
            Checker(loc.getLocation(), ENTITY_EXPLODE, () -> event.setCancelled(true));
        }
    }


    //↓トロッコやボートなどが対象
    @EventHandler
    public void onVehicleDamageEvent(VehicleDamageEvent event) {
        var loc = event.getVehicle().getLocation();
        Checker(loc, VEHICLE_DAMAGE, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onVehicleEnterEvent(VehicleEnterEvent event) {
        var loc = event.getVehicle().getLocation();
        Checker(loc, VEHICLE_ENTER, () -> event.setCancelled(true));
    }
}
