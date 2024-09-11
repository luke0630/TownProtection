package com.townprotection.AfterGUI.Town.Marked;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.System.CallBackStringByChat;
import com.townprotection.TownProtection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Selector.Selector.ChangeMarkedDataSelector;
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;

public class Marked_Data_Editor extends GUIAbstract<GuiManager.GUi> {

    @Override
    public GuiManager.GUi getType() {
        return GuiManager.GUi.MARK_DATA_EDITOR;
    }

    @Override
    public Inventory getInventory(Player player) {
        var markData = playerOpenGUI.get(player).targetTownMarkData;
        var inv = getInv(9*3, toColor("&c&l" + markData.getName() + "&8&lの編集"));
        var item = getItem(Material.OAK_LOG, markData.getName());
        var changeMarkName = getItem(Material.NAME_TAG, "&a&l土地の名前を変更する");

        var teleportTown = getItem(Material.COMPASS, "テレポートする");
        setLore(teleportTown, List.of(
                "&c&lスペクテイターである必要があります。"
        ));
        inv.setItem(9*2+8, teleportTown);

        var owner = getPlayerHead( markData.getOwner() );
        var allowedPlayers = getItem(Material.REDSTONE, "&9&l許可者リスト");
        var manager = getItem(Material.REDSTONE_LAMP, "&6&l管理者リスト");
        var back = getItem(Material.FEATHER, "&c&l戻る");
        var allowList = getItem(Material.LEVER, "&a&l許可行動を選択");

        setLore(manager, List.of(
                "&c&lクリックしてこの土地の管理者を編集",
                "&6&l※管理者は、&c&l土地のルールが適用されず、",
                "&c&l土地の設定が変更可能&f&lです。"
        ));
        setLore(allowedPlayers, List.of(
                "&c&lクリックしてこの土地の許可者を編集",
                "&9&l※許可者には、&c&lルール(保護)が適用されなくなります。"
        ));
        setLore(owner, List.of(
                "&c&lオーナー"
        ));
        setLore(changeMarkName, List.of(
                "&c&l現在の名前: &f&l" + markData.getName(),
                "&c&lクリックして変更する"
        ));
        setLore(allowList, List.of(
                "&c&l許可行動は、&f&l土地内でしてもよい行動を選択できます。",
                "&6&l※チェストを開く...など"
        ));

        if(TownProtection.IsMarkedAdmin(player, markData)) {
            inv.setItem(9+1, changeMarkName);
            inv.setItem(9+2, allowList);
            inv.setItem(9+7, allowedPlayers);
            inv.setItem(9+8, manager);
            inv.setItem(9*2+4, getItem(Material.WOODEN_PICKAXE, "&6&l範囲を変更する"));
        }
        inv.setItem(9+4, item);
        inv.setItem(9+6, owner);
        inv.setItem(0, back);
        inv.setItem(9*2, getItem(Material.BARRIER, "&c&l土地を削除する"));

        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent inventoryClickEvent) {
        var slot = inventoryClickEvent.getSlot();
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        var guiData = (GUIData) playerOpenGUI.get(player).clone();
        if(slot == 0) {
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_LIST);
        }
        if(slot == 9+1) {
            String message = TownProtection.message + "新しい土地の名前をチャットに入力して送信してください。";
            CallBackStringByChat.SetName(player, message, (Object s) -> {
                if(s instanceof String result){
                    if(IsAlreadyExistMarkedName(guiData.targetTownData, result)) {
                        //既に存在します
                        player.sendMessage(TownProtection.message + result + toColor(" &c&lこの名前はすでに使われているため使用不可能です。"));
                    } else {
                        guiData.targetTownMarkData.setName(result);
                        player.sendMessage(TownProtection.message + result + " に変更しました。");
                        playerOpenGUI.put(player, guiData);
                        getManager().OpenGUI(player, getType());
                    }
                }
            });
        }
        if(slot == 9+2) {
            var data = playerOpenGUI.get(player);
            data.targetActionList = data.targetTownMarkData.allowActionList;
            data.backGUI = getType();
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.ACTION_CURRENT_LIST);
        }
        if(slot == 9+7) {
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_LIST);
        }
        if(slot == 9+8) {
            playerOpenGUI.get(player).targetData = playerOpenGUI.get(player).targetTownMarkData;
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.MANAGER_CURRENT_LIST);
        }
        if(slot == 9*2+8) {
            TeleportSelectorData(player, guiData.targetTownMarkData.selectorData);
        }
        if(slot == 9*2) {
            TownProtection.getManager().OpenGUI(player, GuiManager.GUi.MARK_DATA_DELETE);
        }
        if(slot == 9*2+4) {
            ChangeMarkedDataSelector(player, guiData.targetTownMarkData);
        }
    }
}
