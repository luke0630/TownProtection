package com.townprotection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Useful {
    public static String toColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static ItemStack getItem(Material material, String name) {
        var item = new ItemStack(material);
        var meta = item.getItemMeta();

        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        var meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String getXYZMessage(Location location) {
        return toColor("X: "+location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ());
    }
}
