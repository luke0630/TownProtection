package com.townprotection.GUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.townprotection.Useful.*;
import static com.townprotection.Data.MainData.*;

public class ShowListGUI {
    static final Integer PAGE_MAX_ITEM = 45;
    public static Inventory getListGUI(Player player) {
        var guiData = playerOpenGUI.get(player).listData;
        var type = guiData.type;

        int needsPage = (Integer)guiData.showItem.size() / PAGE_MAX_ITEM;

        if(!listPage.containsKey(player)) {
            listPage.put(player, 0);
        }
        int currentPage = listPage.get(player);

        var perPageMinItemIndex = PAGE_MAX_ITEM*currentPage;
        //player.sendMessage("min" + perPageMinItemIndex);
        var perPageMaxItemIndex = PAGE_MAX_ITEM*currentPage+PAGE_MAX_ITEM;
        //player.sendMessage("max" + perPageMaxItemIndex);

        Inventory inv = null;
        if(needsPage > 0) {
            var result = guiData.guiName + " ページ(" + currentPage + "/" + needsPage + ")";
            inv = getInv(9*6, result);
        } else {
            inv = getInv(9*6, guiData.guiName);
        }

        int counter = 0;
        for(int i=perPageMinItemIndex;i < perPageMaxItemIndex;i++) {
            if(i < guiData.showItem.size()) {
                var item = guiData.showItem.get(i);
                if (item != null) {
                    inv.setItem(counter, item);
                    counter++;
                }
            }
        }
        var back = getItem(Material.FEATHER, "&c&l戻る");
        var interaction = getItem(Material.REDSTONE_BLOCK, guiData.interactionName);
        switch(type) {
            case NONE -> {

            }
            case BOTTOM_BACK -> {
                setBottomControlOnGUI(inv);
                inv.setItem(9*5, back);
            }
            case BOTTOM_INTERACTION -> {
                setBottomControlOnGUI(inv);
                inv.setItem(9*5+4, interaction);
            }
            case BOTTOM_BACK_INTERACTION -> {
                setBottomControlOnGUI(inv);
                inv.setItem(9*5, back);
                inv.setItem(9*5+4, interaction);
            }
        }


        if(currentPage < needsPage) {
            inv.setItem(9*5+8, getItem(Material.FEATHER, "&c&l次のページ"));
        }
        if(currentPage > 0) {
            inv.setItem(9*5+7, getItem(Material.FEATHER, "&c&l前のページ"));
        }

        return inv;
    }
}
