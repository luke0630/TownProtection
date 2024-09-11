package com.townprotection.AfterGUI.Town;

import com.townprotection.Data.MainData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.TownProtection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.TownProtection.message;
import static com.townprotection.Useful.*;

public class TownEditor_ChangeMayor extends GUIAbstract<GuiManager.GUi> {
    @Override
    public Enum<GuiManager.GUi> getType() {
        return GuiManager.GUi.TOWN_CHANGE_MAYOR;
    }

    @Override
    public Inventory getInventory(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var inv = getInv(9*3, "&c&l確認画面 - 市長を変更");
        var nextMayorHead = getPlayerHead(data.nextMayor);
        var currentMayorHead = getPlayerHead( data.targetTownData.getOwner() );

        var ok = getItem(Material.REDSTONE, "&c&l変更する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&l市名: " + data.targetTownData.getName(),
                "&f&lクリックすると、市長交代が行われます。",
                "&c&l" + Bukkit.getOfflinePlayer(data.targetTownData.getOwner()).getName() + "&f&l → &c&l" + Bukkit.getOfflinePlayer(data.nextMayor).getName()
        ));

        inv.setItem(3, currentMayorHead);
        inv.setItem(5, nextMayorHead);

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);


        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if(slot == 9+3) {
            player.sendMessage(TakoUtility.toColor(message + "市長を" + Bukkit.getOfflinePlayer(playerOpenGUI.get(player).nextMayor) .getName() + "に変更しました"));
            playerOpenGUI.get(player).targetTownData.setOwner(playerOpenGUI.get(player).nextMayor);
            TownProtection.Save();
            player.closeInventory();
        }
        else if(slot == 9+5) {
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        }
    }
}
