package com.townprotection.AfterGUI.Town.Effect;

import com.townprotection.Data.MainData;
import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.GUI.EffectGUI;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.System.CallBackStringByChat;
import com.townprotection.TownProtection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.luke.yakisobaGUILib.Abstract.GUIAbstract;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.GUI.GuiManager.openGUI;
import static com.townprotection.TownProtection.message;
import static com.townprotection.Useful.*;

public class TownEditor_EffectEditor extends GUIAbstract<GuiManager.GUi> {

    @Override
    public Enum<GuiManager.GUi> getType() {
        return GuiManager.GUi.TOWN_EFFECT_EDITOR;
    }

    @Override
    public Inventory getInventory(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var effectData = data.targetEffectData;
        var inv = getInv(9*3, "&c&l演出の編集");

        var effectIcon = getItem(effectData.getInfo().getIcon(), effectData.getInfo().getTitle());
        setLore(effectIcon, effectData.getInfo().getDescription());
        var back = getItem(Material.FEATHER, "&c&l戻る");
        var delete = getItem(Material.BARRIER, "&c&l削除する");

        inv.setItem(0, back);
        inv.setItem(9+4, effectIcon);
        inv.setItem(9*2, delete);

        new EffectGUI().SetEffectGUI(effectData, inv);

        return inv;
    }

    @Override
    public void InventoryClickListener(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        var slot = event.getSlot();
        var openData = playerOpenGUI.get(player).clone();
        var rawData = playerOpenGUI.get(player);
        var effectData = openData.targetEffectData;
        var manager = TownProtection.getManager();
        if(slot == 0) {
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
            return;
        } else if(slot == 9*2) {
            rawData.targetTownData.effectList.remove(rawData.targetEffectData);
            player.sendMessage(String.valueOf(rawData.targetTownData.effectList.size()));
            new CallBackListener().UpdateEffect();
            TownProtection.Save();
            manager.OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
            return;
        }
        if(effectData instanceof ShowTitle showTitleData) {
            if(slot == 9+1) {
                if(event.isRightClick()) {
                    showTitleData.setTitleEnable(!showTitleData.isTitleEnable());
                    playerOpenGUI.put(player, openData);
                    openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    TownProtection.Save();
                } else {
                    CallBackStringByChat.SetName(player, message + "表示させる”タイトル”をチャットに入力して送信してください", (Object s) -> {
                        String message = (String) s;

                        showTitleData.setTitleMessage(message);
                        playerOpenGUI.put(player, openData);
                        manager.OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    });
                }

            } else if(slot == 9+2) {
                if(event.isRightClick()) {
                    showTitleData.setSubTitleEnable(!showTitleData.isSubTitleEnable());
                    playerOpenGUI.put(player, openData);
                    manager.OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    TownProtection.Save();
                } else {
                    CallBackStringByChat.SetName(player, message + "表示させる”サブタイトル”をチャットに入力して送信してください", (Object s) -> {
                        String message = (String) s;

                        showTitleData.setSubTitleMessage(message);
                        playerOpenGUI.put(player, openData);
                        manager.OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    });
                }
            } else if(slot == 9+6) {
                if(event.isRightClick()) {
                    showTitleData.setSayMessageEnable(!showTitleData.isSayMessageEnable());
                    playerOpenGUI.put(player, openData);
                    manager.OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    TownProtection.Save();
                } else {
                    CallBackStringByChat.SetName(player, message + "表示させる”メッセージ”をチャットに入力して送信してください", (Object s) -> {
                        String message = (String) s;

                        showTitleData.setSayMessage(message);
                        playerOpenGUI.put(player, openData);
                        manager.OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                    });
                }
            }

        }
    }
}
