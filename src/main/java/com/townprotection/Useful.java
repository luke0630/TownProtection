package com.townprotection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Selector.Selector.actionBarSchedulers;

public class Useful {
    public static String toColor(String message) {
        if(message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Inventory getInv(Integer size, String title) {
        return Bukkit.createInventory(null,size,Useful.toColor(title));
    }

    public static ItemStack getItem(Material material, String name) {
        var item = new ItemStack(material);
        var meta = item.getItemMeta();

        meta.setDisplayName(toColor(name));

        item.setItemMeta(meta);
        return item;
    }

    public static List<String> uuidToString(List<UUID> uuidList) {
        var list = new ArrayList<String>();
        for(var string : uuidList) {
            list.add(string.toString());
        }
        return list;
    }

    public static List<UUID> stringToUUID(List<String> stringList) {
        var list = new ArrayList<UUID>();
        for(var string : stringList) {
            list.add(UUID.fromString(string));
        }
        return list;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        var meta = item.getItemMeta();
        var resultLore = new ArrayList<String>();
        for(var l : lore) {
            resultLore.add(toColor(l));
        }
        meta.setLore(resultLore);
        item.setItemMeta(meta);
    }

    public static String getXYZMessage(Location location) {
        return toColor("&c&lX: "+location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ());
    }

    public static String getBooleanInJapanese(Boolean bool) {
        if(bool) {
            return "&b&l有効";
        } else {
            return "&c&l無効";
        }
    }

    public static void ShowActionBar(Player player, String message) {
        HiddenActionBar(player);
        actionBarSchedulers.put(player, Bukkit.getScheduler().runTaskTimerAsynchronously(TownProtection.instance, () -> {
            player.sendActionBar(toColor(message));
        }, 0, 20L));
    }

    public static void HiddenActionBar(Player player) {
        if(actionBarSchedulers.containsKey(player)) {
            actionBarSchedulers.get(player).cancel();
            actionBarSchedulers.remove(player);
        }
    }

    public static void setBottomControlOnGUI(Inventory inventory) {
        var size = inventory.getSize();
        int lines = size / 9;
        lines--; //index対応
        for(int i=lines*9;i < size;i++) {
            inventory.setItem(i,Useful.getItem(Material.BLACK_STAINED_GLASS_PANE," "));
        }
    }

    public static ItemStack getPlayerHead(UUID uuid) {
        ItemStack skull = getItem(Material.PLAYER_HEAD,"&f&l"+ Bukkit.getOfflinePlayer(uuid).getName());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(skullMeta);
        return skull;
    }


    public static Location getPistonNextLocation(Location previousLoc, BlockFace face) {
        var changeData = previousLoc.clone();
        if(face == BlockFace.NORTH) {
            changeData.setZ(previousLoc.getBlockZ()-1);
        } else if(face == BlockFace.EAST) {
            changeData.setX(previousLoc.getBlockX()-1);
        } else if(face == BlockFace.SOUTH) {
            changeData.setZ(previousLoc.getBlockZ()+1);
        } else if(face == BlockFace.WEST) {
            changeData.setX(previousLoc.getBlockX()+1);
        }

        return changeData;
    }
}
