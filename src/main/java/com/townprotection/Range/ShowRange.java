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

import static com.townprotection.Selector.Selector.showRange;

public class ShowRange {
    public static Map<Player, List<Block>> blocks = new HashMap<>();
    public static void PlaceFakeBlock(Player player, Location location, Material material) {
        var y = player.getWorld().getHighestBlockAt(location);
        var clone = location.clone();
        clone.setY(y.getY());

        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0, new BlockPosition(clone.getBlockX(), clone.getBlockY(), clone.getBlockZ()));
        packet.getBlockData().write(0, WrappedBlockData.createData(material.createBlockData()));

        var blockData = clone.getBlock();
        if(blocks.containsKey(player)) {
            blocks.get(player).add(blockData);
        } else {
            blocks.put(player, new ArrayList<>(List.of(blockData)));
        }

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }


    public static void ShowRangeWithBlock(Player player, SelectorData data, Material material) {
        showRange(player, data, (Object object) -> {
            if(object instanceof Location location) {
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
        blocks.remove(player);
    }
}
