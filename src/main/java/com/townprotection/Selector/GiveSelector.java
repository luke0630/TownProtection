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
        player.getInventory().addItem( wand );
    }
}
