package com.townprotection.AfterGUI.Town;

import com.townprotection.Data.MainData;
import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.getInv;
import static com.townprotection.Useful.getItem;

public class TownEditor_ModeSelect extends GUIAbstract<GuiManager.GUi> {
    @Override
    public Enum<GuiManager.GUi> getType() {
        return GuiManager.GUi.TOWN_ICON_MODE_SELECT;
    }

    @Override
    public Inventory getInventory(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var inv = getInv(9*3, "&c&lアイコン変更画面");

        var townData = data.targetTownData;

        var fromInv = getItem(Material.CHEST, "&6&lインベントリのアイテムからアイコンを指定");
        var fromList = getItem(Material.STONE, "&a&lブロック一覧からアイコンを指定");

        inv.setItem(4, getItem(townData.getIcon(), townData.getName()));

        inv.setItem(0, getItem(Material.FEATHER, "&c&l戻る"));
        inv.setItem(9+3, fromList);
        inv.setItem(9+5, fromInv);

        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if(slot == 0) {
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        } else if(slot == 9+3) {
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_ICON_BLOCK_LIST);
        } else if (slot == 9+5) {
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_ICON_PLAYER_INVENTORY_LIST);
        }
    }
}
