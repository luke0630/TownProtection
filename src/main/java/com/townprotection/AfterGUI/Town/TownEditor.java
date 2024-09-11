package com.townprotection.AfterGUI.Town;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MainData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.System.CallBackStringByChat;
import com.townprotection.TownProtection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Selector.Selector.ChangeTownSelectorData;
import static com.townprotection.TownProtection.IsAlreadyExistTownName;
import static com.townprotection.TownProtection.TeleportSelectorData;
import static com.townprotection.Useful.*;

public class TownEditor extends GUIAbstract<GuiManager.GUi> {

    @Override
    public GuiManager.GUi getType() {
        return GuiManager.GUi.TOWN_EDITOR;
    }

    @Override
    public Inventory getInventory(Player player) {
        var townData = MainData.playerOpenGUI.get(player).targetTownData;
        var guiName = "&c&l" + townData.getName() + "&8&lの編集(町)";
        if(!TownProtection.IsTownAdmin(player, townData)) {
            guiName = "&8&l" + townData.getName() + "の詳細";
        }
        var inv = getInv(9*3, guiName);

        var townIcon = getItem(townData.getIcon(), townData.getName());

        var allowList = getItem(Material.LEVER, "&a&l許可行動を選択");
        setLore(allowList, List.of(
                "&c&l許可行動は、&f&l土地内でしてもよい行動を選択できます。",
                "&6&l※チェストを開く...など"
        ));



        inv.setItem(0, getItem(Material.FEATHER, "&c&l戻る"));

        var townMayorItem = getPlayerHead(townData.getOwner());
        setLore(townMayorItem, List.of(
                "&c&l市長: " + Bukkit.getOfflinePlayer(townData.getOwner()).getName()
        ));
        inv.setItem(9+7, townMayorItem);

        var teleportTown = getItem(Material.COMPASS, "テレポートする");
        setLore(teleportTown, List.of(
                "&c&lスペクテイターである必要があります。"
        ));
        inv.setItem(9*2+8, teleportTown);

        if(TownProtection.IsTownAdmin(player, townData)) {
            inv.setItem(9+2, getItem(Material.OAK_LOG, "&c&l土地を管理"));
            inv.setItem(9+1, getItem(Material.SCULK_SENSOR, "&b&l演出を管理"));
            if(player.getUniqueId().toString().equalsIgnoreCase(townData.getOwner().toString())) {
                setLore(townIcon, List.of(
                        "&c&lクリックしてアイコンを変更"
                ));
                var mayor = getItem(Material.PLAYER_HEAD, "&c&l市長: &f&l" + Bukkit.getOfflinePlayer(townData.getOwner()).getName() );
                setLore(mayor, List.of("&c&lクリックして市長を交代する"));
                inv.setItem(9+7, mayor);
                inv.setItem(9+6, getItem(Material.NAME_TAG, "&c&l名前を変更する"));
                inv.setItem(9+8, getItem(Material.REDSTONE_LAMP, "&c&l町の管理者を追加する"));
                inv.setItem(9*2, getItem(Material.BARRIER, "&c&lこの町を削除する"));
                inv.setItem(9*2+4, getItem(Material.WOODEN_PICKAXE, "&6&l範囲を変更する"));
                inv.setItem(9*2+2, allowList);
            }
        } else {
            inv.setItem(9+8, getItem(Material.REDSTONE_LAMP, "&c&l町の管理者の一覧"));
            inv.setItem(9+2, getItem(Material.OAK_LOG, "&c&l土地の一覧"));
        }

        inv.setItem(9+4, townIcon);

        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        var slot = inventoryClickEvent.getSlot();
        var townData = playerOpenGUI.get(player).targetTownData;
        var manager = TownProtection.getManager();;
        if(slot == 0) {
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_LIST);
        }
        else if(slot == 9+1) {
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
        }
        else if(slot == 9+2) {
            //町の土地リストを表示するGUIに移行
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_LIST);
        }
        else if(slot == 9+4) {
            if(TownProtection.IsTownAdmin(player, townData)) {
                manager.OpenGUI(player, GuiManager.GUi.TOWN_ICON_MODE_SELECT);
            }
        }
        else if(slot == 9+6) {
            if(TownProtection.IsTownAdmin(player, townData)) {
                GUIData guiData = playerOpenGUI.get(player).clone();
                String message = TownProtection.message + "新しい町の名前をチャットに入力して送信してください。";
                CallBackStringByChat.SetName(player, message, (Object s) -> {
                    if(s instanceof String result){
                        if(IsAlreadyExistTownName(result)) {
                            //既に存在します
                            player.sendMessage(TownProtection.message + result + toColor(" &c&lこの名前はすでに使われているため使用不可能です。"));
                        } else {
                            guiData.targetTownData.setName(result);
                            player.sendMessage(TownProtection.message + result + " に変更しました。");
                            playerOpenGUI.put(player, guiData);
                            manager.OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
                        }
                    }
                });
            }
        }
        else if(slot == 9+7) {
            if(TownProtection.IsTownAdmin(player, townData)) {
                manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_SELECT_MAYOR);
            }
        }
        else if(slot == 9+8) {
            playerOpenGUI.get(player).targetData = playerOpenGUI.get(player).targetTownData;
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.MANAGER_CURRENT_LIST);
        }
        if(slot == 9*2) {
            manager.OpenGUI(player, GuiManager.GUi.TOWN_DELETE_CONFIRM);
        }
        if(slot == 9*2+2) {
            var data = playerOpenGUI.get(player);
            data.targetActionList = data.targetTownData.allowActionList;
            data.backGUI = getType();
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.ACTION_CURRENT_LIST);
        }
        if(slot == 9*2+4) {
            var currentOpenTown = playerOpenGUI.get(player).targetTownData;
            ChangeTownSelectorData(player, currentOpenTown);
        }
        if(slot == 9*2+8) {
            TeleportSelectorData(player, townData.getSelectorData());
        }
    }
}
