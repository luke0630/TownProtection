package com.townprotection.Listener;

import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Selector.GiveSelector;
import com.townprotection.Selector.Selector;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import java.util.Iterator;
import java.util.List;

import static com.townprotection.Useful.toColor;
import static org.bukkit.event.block.Action.LEFT_CLICK_BLOCK;

public class BlockBreakListener implements org.bukkit.event.Listener {

    void Checker(Player player,Location blockLoc, ActionList.Action action, Runnable callback) {
        for(var town : MainData.townMarkData) {
            var targetPos = new SelectorData();
            targetPos.startBlock = blockLoc;
            targetPos.endBlock = blockLoc;

            if(!Selector.overlaps(town.rangeOfTown, targetPos)) {
            } else {
                //町の中
                var markData = Selector.getMarkDataFromLocation(town, blockLoc);
                if(markData != null) {
                    if(markData.allowActionList.contains(action)) return; //アクションが許可されていたらreturnしてcallbackさせない
                    if(player == null) {
                        callback.run();
                        return;
                    }
                    if(!TownProtection.IsMarkedAdmin(player, markData)) {
                        player.sendMessage(TownProtection.message + toColor("&c&lこの行動は許可されていません。"));
                        callback.run();
                        return;
                    }
                } else {
                    if(player != null && !TownProtection.IsTownAdmin(player, town)) {
                        player.sendMessage(TownProtection.message + toColor("&c&l町の変更は許可されません。"));
                    }
                    callback.run();
                }
            }
        }
    }

    final ActionList.Action BREAK_BLOCK = ActionList.Action.PLAYER_BREAK_BLOCK;
    final ActionList.Action PLACE_BLOCK = ActionList.Action.PLAYER_PLACE_BLOCK;
    final ActionList.Action PLAYER_INTERACT = ActionList.Action.PLAYER_INTERACT;
    final ActionList.Action DAMAGE_ENTITY_BY_ENTITY = ActionList.Action.ENTITY_DAMAGE_TO_ENTITY;
    final ActionList.Action PLAYER_TO_PLAYER_DAMAGE = ActionList.Action.PLAYER_PVP;
    final ActionList.Action HANGING_BREAK = ActionList.Action.HANGING_BREAK;
    final ActionList.Action ENTITY_EXPLODE = ActionList.Action.TNT_EXPLOSION;
    final ActionList.Action VEHICLE_DAMAGE = ActionList.Action.VEHICLE_DAMAGE;
    final ActionList.Action VEHICLE_ENTER = ActionList.Action.VEHICLE_ENTER;

    @EventHandler
    public void onDamageEntity(EntityDamageByEntityEvent event) {
        var damager = event.getDamager(); //与えた人
        var target = event.getEntity().getLocation(); //ダメージを与えられた人

        if(damager instanceof Player player) {
            if(target instanceof Player) {
                Checker(player, target, PLAYER_TO_PLAYER_DAMAGE,  () -> event.setCancelled(true));
                return;
            }
            Checker(player, target, DAMAGE_ENTITY_BY_ENTITY,  () -> event.setCancelled(true));
        } else if(damager.getType() == EntityType.PRIMED_TNT) {
            Checker(null, target, DAMAGE_ENTITY_BY_ENTITY,  () -> event.setCancelled(true));
        }
    }

    //**********ピストン**********//
    @EventHandler
    public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) {
        PistonProtectMethod(event.getBlocks(), event.getBlock(), () -> event.setCancelled(true));
    }

    @EventHandler
    public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) {
        PistonProtectMethod(event.getBlocks(), event.getBlock(), () -> event.setCancelled(true));
    }
    //**************************//

    //**********額縁************//
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) {
        var remover = event.getRemover(); //プレイヤー
        var target = event.getEntity().getLocation(); //その場所

        if(remover instanceof Player player) {
            Checker(player, target, HANGING_BREAK, () -> event.setCancelled(true));
        }
    }
    @EventHandler
    public void onHangingBreakByEntityEvent(HangingBreakEvent event) {
        var target = event.getEntity();
        var cause = event.getCause();
        if (cause == HangingBreakEvent.RemoveCause.EXPLOSION) {
            Checker(null, target.getLocation(), HANGING_BREAK, () -> event.setCancelled(true));
        }
    }
    //*************************//


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var action = event.getAction();
        if(event.getClickedBlock() == null) return;
        if(player.getInventory().getItemInMainHand().getType() == Material.WOODEN_PICKAXE) {
            if (player.getInventory().getItemInMainHand().getLore().get(0).equalsIgnoreCase(GiveSelector.SELECTOR_LORE)) return;
        }
        if(action == Action.RIGHT_CLICK_BLOCK) {
            var blockLoc = event.getClickedBlock().getLocation();
            Checker(player, blockLoc, PLACE_BLOCK, () -> event.setCancelled(true));
        }
        else
        if(action == LEFT_CLICK_BLOCK) {
            var blockLoc = event.getClickedBlock().getLocation();
            Checker(player, blockLoc, BREAK_BLOCK, () -> event.setCancelled(true));

            var clickBlock = event.getClickedBlock();
            var denyType = new Material[]{
                    Material.CHEST,
                    Material.TRAPPED_CHEST,
                    Material.HOPPER,

            };
            //Checker(player, blockLoc, PLAYER_INTERACT, () -> event.setCancelled(true));
        }
    }
    //TNTなど
    /*@EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event) {
        var locList = event.blockList();
        for(var loc : locList) {
            Checker(null, loc.getLocation(), ENTITY_EXPLODE, () -> event.blockList().clear());
        }
    }*/

    /*@EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        var entity = event.getEntity();
        Checker(null, entity.getLocation(), ENTITY_EXPLODE, () -> event.setCancelled(true));
    }*/

    @EventHandler
    public void onExplostionTNT(EntityExplodeEvent event) {
        var blocks = event.blockList();
        var player = Bukkit.getPlayer("Luke0630");

        Iterator<Block> iterator = event.blockList().iterator();

        while (iterator.hasNext()) {
            player.sendMessage(String.valueOf(event.blockList().size()));
            var block = iterator.next();

            var townData = Selector.getTownFromLocation(block.getLocation());
            if(townData != null) {
                var markedData = Selector.getMarkDataFromLocation(townData, block.getLocation());
                if(markedData != null) {
                    if(markedData.allowActionList.contains(ActionList.Action.TNT_EXPLOSION)) {
                        continue;
                    }
                    iterator.remove();
                } else {
                    iterator.remove();
                }
            } else {
                //町の外
            }
        }
    }

    //↓トロッコやボートなどが対象
    @EventHandler
    public void onVehicleDamageEvent(VehicleDamageEvent event) {
        var loc = event.getVehicle().getLocation();
        var player = event.getAttacker();
        Checker((Player) player, loc, VEHICLE_DAMAGE, () -> event.setCancelled(true));
    }

    @EventHandler
    public void onVehicleEnterEvent(VehicleEnterEvent event) {
        var loc = event.getVehicle().getLocation();
        var player = event.getEntered();
        Checker((Player) player, loc, VEHICLE_ENTER, () -> event.setCancelled(true));
    }


    void PistonProtectMethod(List<Block> locList, Block piston, Runnable callback) {

        var pistonLoc = piston.getLocation();

        var targetPos = new SelectorData();
        targetPos.startBlock = pistonLoc;
        targetPos.endBlock = pistonLoc;

        BlockFace facing = null;
        var blockdata = (BlockData)piston.getBlockData();
        if (blockdata instanceof Directional directional) {
            facing = directional.getFacing();
        }

        TownData pistonTown = null;
        for(var town : MainData.townMarkData) {
            if(Selector.overlaps(town.rangeOfTown, targetPos)) {
                pistonTown = town;
            }
        }

        SelectorData pistonSelector = null;
        if(pistonTown != null) {
            for(var mark : pistonTown.selectorMarkData) {
                if(Selector.overlaps(mark.selectorData, targetPos)) {
                    pistonSelector = mark.selectorData;
                }
            }
        }

        boolean isTargetBlocksOutOfMarked = false;

        boolean contain = false;

        for(var data : locList) {
            var townData = Selector.getTownFromLocation(data.getLocation());
            if(townData == null) {
            } else {
                var markData = Selector.getMarkDataFromLocation(townData, data.getLocation());
                if(pistonSelector == null && markData == null) {
                    var lastData = locList.get(locList.size()-1).getLocation().clone();
                    var nextData = Useful.getPistonNextLocation(lastData, facing);
                    var thatMarkData = Selector.getMarkDataFromLocation(townData, nextData);
                    if(thatMarkData != null) {
                        callback.run();
                        return;
                    }
                    return;
                } else if(pistonTown == null) {
                    callback.run();
                    return;
                }
                else {
                    var lastData = locList.get(locList.size()-1).getLocation().clone();
                    var nextData = Useful.getPistonNextLocation(lastData, facing);
                    var thatMarkData = Selector.getMarkDataFromLocation(townData, nextData);
                    if(thatMarkData == null) {
                        callback.run();
                        return;
                    }
                }
            }
        }
        if(pistonTown == null && !contain ) {
            isTargetBlocksOutOfMarked = true;
        }
        if(pistonTown != null && pistonSelector != null) {
            for(var targets : locList) {
                var pos = new SelectorData();
                pos.startBlock = targets.getLocation();
                pos.endBlock = targets.getLocation();

                if(Selector.getMarkDataFromLocation(pistonTown, targets.getLocation()) == null) {
                    isTargetBlocksOutOfMarked = true;
                    break;
                }

                /*var targetTown = Selector.getTownFromLocation(targets.getLocation());
                if(targetTown == null) {
                    break;
                } else {
                    var targetMark = Selector.getMarkDataFromLocation(targetTown, targets.getLocation());
                    if(targetMark == null) {
                        isTargetBlocksOutOfMarked = true;
                        break;
                    }
                }*/


                if(!Selector.overlaps(pistonSelector, pos)) {
                    isTargetBlocksOutOfMarked = true;
                    break;
                }
            }
        }
        if(pistonTown != null && pistonSelector == null) { //町の中だけど土地の中ではない場合
            for(var marked : pistonTown.selectorMarkData) {
                isTargetBlocksOutOfMarked = false;
                for(var blocks : locList) {
                    var pos = new SelectorData();
                    pos.startBlock = blocks.getLocation();
                    pos.endBlock = blocks.getLocation();
                    if(Selector.overlaps(marked.selectorData, pos)) {
                        isTargetBlocksOutOfMarked = true;
                        break;
                    }
                }
            }
        }
        if(pistonTown == null) {
            return;
        }

        if(isTargetBlocksOutOfMarked) {
            callback.run();
        }
    }
}
