package com.townprotection.GUI.Town;

import com.townprotection.Data.MainData;
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
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;
import static com.townprotection.Useful.getItem;

public class TownEditor_Delete extends GUIAbstract<GuiManager.GUi> {

    @Override
    public GuiManager.GUi getType() {
        return GuiManager.GUi.TOWN_DELETE_CONFIRM;
    }

    @Override
    public Inventory getInventory(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var inv = getInv(9*3, "&c&l確認画面 - 町を削除する");

        var townData = data.targetTownData;

        var ok = getItem(Material.REDSTONE, "&c&l町を削除する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&lクリックすると、町が削除されます(ブロックなどに変更はありません。)"
        ));

        inv.setItem(4, getItem(townData.getIcon(), townData.getName()));

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);
        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        var slot = inventoryClickEvent.getSlot();

        if(slot == 9+3) {
            var targetTown = playerOpenGUI.get(player).targetTownData;
            player.sendMessage(message + targetTown.getName() + "を削除しました。");
            townMarkData.remove(targetTown); //削除する
            TownProtection.Save();
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_LIST);
        } else if(slot == 9+5) {
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        }
    }
}
