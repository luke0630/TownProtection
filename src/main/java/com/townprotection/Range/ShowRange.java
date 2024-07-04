package com.townprotection.Range;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.townprotection.Data.SelectorData.SelectorData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.townprotection.Selector.Selector.schedulers;
import static com.townprotection.Selector.Selector.showRange;

public class ShowRange {
    public static Map<Player, List<Block>> blocks = new HashMap<>();
        public static void PlaceFakeBlock(Player player, Location location, Material material) {
        for(var loc : getHighBlock(location, 2)) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            packet.getBlockPositionModifier().write(0, new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            packet.getBlockData().write(0, WrappedBlockData.createData(material.createBlockData()));

            var blockData = loc.getBlock();
            if(blocks.containsKey(player)) {
                blocks.get(player).add(blockData);
            } else {
                blocks.put(player, new ArrayList<>(List.of(blockData)));
            }

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        }
    }


    public static void setOnlyEdgeBlock(Player player, SelectorData data) {

        var startX = data.startBlock.clone();
        var startZ = data.startBlock.clone();
        var endX = data.endBlock.clone();
        var endZ = data.endBlock.clone();
        var signumX = Math.signum(data.endBlock.getBlockX() - data.startBlock.getBlockX());
        var signumZ = Math.signum(data.endBlock.getBlockZ() - data.startBlock.getBlockZ());
        startX.setX(startX.getBlockX()+signumX);
        startZ.setZ(startZ.getBlockZ()+signumZ);

        PlaceFakeBlock(player, data.startBlock, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, startX, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, startZ, Material.GOLD_BLOCK);

        startX.setX(startX.getBlockX()+signumX);
        startZ.setZ(startZ.getBlockZ()+signumZ);

        PlaceFakeBlock(player, startX, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, startZ, Material.GOLD_BLOCK);




        endX.setX(endX.getBlockX()+(-1*signumX));
        endZ.setZ(endZ.getBlockZ()+(-1*signumZ));

        PlaceFakeBlock(player, data.endBlock, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, endX, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, endZ, Material.GOLD_BLOCK);


        endX.setX(endX.getBlockX()+(-1*signumX));
        endZ.setZ(endZ.getBlockZ()+(-1*signumZ));

        PlaceFakeBlock(player, endX, Material.GOLD_BLOCK);
        PlaceFakeBlock(player, endZ, Material.GOLD_BLOCK);
    }
    public static List<Location> getHighBlock(Location location, int count) {
        int counter = 0;
        List<Location> locationList = new ArrayList<>();
        for(int i=320;i > -64;i--) {
            var checkLoc = location.clone();
            checkLoc.setY(i);
            var checkBlock = checkLoc.getBlock();
            if(checkBlock.getType().isCollidable()) {
                if(counter < count) {
                    counter++;
                    locationList.add(checkBlock.getLocation());
                } else {
                    break;
                }
            }
        }
        if(!locationList.isEmpty()) {
            return locationList;
        }
        return locationList;
    }
    public static void ShowRangeWithBlock(Player player, SelectorData data, Material material, boolean isRemove) {
        if(isRemove) {
            RemoveShowRange(player);
        }
        var blocks = showRange(player, data, null);
        if(blocks.size() >= 250) {
            setOnlyEdgeBlock(player, data);
            return;
        }
        showRange(player, data, (Object s) -> {
            if(s instanceof Location location) {
                PlaceFakeBlock(player, location, material);
            }
        });
     }

    public static void RemoveShowRange(Player player) {
        if(!blocks.containsKey(player)) return;
        for(var block : blocks.get(player)) {
            var location = block.getLocation().clone();
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
            packet.getBlockPositionModifier().write(0, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
            packet.getBlockData().write(0, WrappedBlockData.createData(block.getType()));

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);

        }
        blocks.get(player).clear();
        blocks.remove(player);
    }
    public static void RemoveParticle(Player player) {
        for(var scheduler : schedulers.values()) {
            for(var sche : scheduler) {
                sche.cancel();
            }
        }
        schedulers.remove(player);
    }
}
