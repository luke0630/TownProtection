package com.townprotection.GUI.ChangeOwner;

import com.townprotection.GUI.GuiManager;
import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.SelectorMarkData;
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

public class ConfirmMayor extends GUIAbstract<GuiManager.GUi> {
    @Override
    public Enum<GuiManager.GUi> getType() {
        return GuiManager.GUi.CHANGE_MAYOR;
    }

    @Override
    public Inventory getInventory(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        player.sendMessage( Bukkit.getOfflinePlayer(playerOpenGUI.get(player).nextMayor).getName());
        var inv = getInv(9*3, "&c&l確認画面 - オーナーを変更");
        var nextMayorHead = getPlayerHead(data.nextMayor);
        var currentMayorHead = getPlayerHead( data.targetData.getOwner() );

        var ok = getItem(Material.REDSTONE_BLOCK, "&c&l変更する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        String dataType = "町";
        if(data.targetData instanceof SelectorMarkData) {
            dataType = "土地";
        }
        setLore(ok, List.of(
                "&f&l名前(" + dataType + "): " + data.targetData.getName(),
                "&f&lクリックすると、オーナー交代が行われます。",
                "&c&l" + Bukkit.getOfflinePlayer(data.targetData.getOwner()).getName() + "&f&l → &c&l" + Bukkit.getOfflinePlayer(data.nextMayor).getName()
        ));

        inv.setItem(3, currentMayorHead);
        inv.setItem(5, nextMayorHead);

        inv.setItem(9+5, ok);
        inv.setItem(9+3, no);


        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        if(slot == 9+5) {
            player.sendMessage(TakoUtility.toColor(message + "オーナーを" + Bukkit.getOfflinePlayer(playerOpenGUI.get(player).nextMayor) .getName() + "に変更しました"));
            playerOpenGUI.get(player).targetData.setOwner(playerOpenGUI.get(player).clone().nextMayor);
            TownProtection.Save();
            getManager().OpenGUI(player, playerOpenGUI.get(player).backGUI);
        }
        else if(slot == 9+3) {
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.SELECT_MAYOR);
        }
    }
}
