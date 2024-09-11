package com.townprotection.AfterGUI.Town.Marked;

import com.townprotection.GUI.GuiManager;
import com.townprotection.TownProtection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Data.MainData.townMarkData;
import static com.townprotection.GUI.GuiManager.GUi.MARK_DATA_DELETE;
import static com.townprotection.GUI.GuiManager.GUi.MARK_DATA_EDITOR;
import static com.townprotection.TownProtection.message;
import static com.townprotection.Useful.*;

public class Marked_Delete extends GUIAbstract<GuiManager.GUi> {
    @Override
    public Enum<GuiManager.GUi> getType() {
        return MARK_DATA_DELETE;
    }

    @Override
    public Inventory getInventory(Player player) {
        var targetMarked = playerOpenGUI.get(player).targetTownMarkData;
        var inv = getInv(9*3, toColor("&c&l土地を削除する"));

        var ok = getItem(Material.REDSTONE, "&c&l土地を削除する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&lクリックすると、土地が削除されます(ブロックなどに変更はありません。)"
        ));

        inv.setItem(4, getItem(Material.OAK_LOG, targetMarked.getName()));

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);

        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if(slot == 9+3) {
            var openData = playerOpenGUI.get(player);
            var townData = openData.targetTownData;
            var targetMarked = openData.targetTownMarkData;
            var townIndex = townMarkData.indexOf(townData);
            townMarkData.get(townIndex).selectorMarkData.remove(targetMarked);
            player.sendMessage(message + "&c&l" + townData.getName() + "の、" + targetMarked.getName() + "(土地)を削除しました。");
            player.closeInventory();
        } else if(slot == 9+5) {
            TownProtection.getManager().OpenGUI(player, MARK_DATA_EDITOR);
        }
    }
}
