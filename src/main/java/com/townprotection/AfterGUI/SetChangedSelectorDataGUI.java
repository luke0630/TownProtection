package com.townprotection.AfterGUI;

import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.GUI.GuiManager.GUi.APPLY_SELECTOR_DATA;
import static com.townprotection.Range.ShowRange.RemoveParticle;
import static com.townprotection.Range.ShowRange.RemoveShowRange;
import static com.townprotection.Range.ShowRangeWhenEnter.ShowTownAndMarked;
import static com.townprotection.Selector.Selector.ApplyChangeSelector;
import static com.townprotection.Selector.Selector.changeSelectorDataPlayer;
import static com.townprotection.Useful.*;
import static com.townprotection.Useful.toColor;

public class SetChangedSelectorDataGUI extends GUIAbstract<GuiManager.GUi> {
    @Override
    public Enum<GuiManager.GUi> getType() {
        return APPLY_SELECTOR_DATA;
    }

    @Override
    public Inventory getInventory(Player player) {
        var inv = getInv(9*3, "&c&l選択範囲を適用する");
        inv.setItem(0, getItem(Material.FEATHER, "&c&l閉じる"));
        inv.setItem(9+3, getItem(Material.REDSTONE_BLOCK, "&a&l範囲を適用する"));
        inv.setItem(9+5, getItem(Material.BARRIER, "&c&l適用しないで、モードから抜け出す"));
        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if(slot == 0) {
            player.closeInventory();
        }
        if(slot == 9+3) {
            ApplyChangeSelector(player);
        } else if(slot == 9+5) {
            RemoveShowRange(player);
            HiddenActionBar(player);
            RemoveParticle(player);
            ShowTownAndMarked(player, changeSelectorDataPlayer.get(player).getTownData(), false);
            changeSelectorDataPlayer.remove(player);
            playerSelectData.remove(player);
            player.closeInventory();
            player.sendMessage(toColor("&c選択範囲の変更をキャンセルしました。"));
        }
    }
}
