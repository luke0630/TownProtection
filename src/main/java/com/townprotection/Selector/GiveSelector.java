package com.townprotection.Selector;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

import static com.townprotection.Useful.*;

public class GiveSelector {
    public final static String SELECTOR_LORE = "TOWN_PROTECTION";
    public static void giveSelector(Player player) {
        var wand = getItem(Material.WOODEN_PICKAXE, toColor("&d&l選択ツール"));
        setLore(wand, List.of(
                SELECTOR_LORE
        ));
        if(player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), wand);
            player.sendMessage(toColor("&cインベントリがいっぱいなため、あなたの場所にツールをドロップしました。"));
        } else {
            player.getInventory().addItem( wand );
            player.sendMessage(toColor("&aツールを与えました！"));
        }
    }
}
