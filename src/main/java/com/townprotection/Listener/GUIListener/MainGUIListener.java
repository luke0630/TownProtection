package com.townprotection.Listener.GUIListener;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.System.CallBackStringByChat;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static com.townprotection.Data.MainData.*;
import static com.townprotection.GUI.GuiManager.openGUI;
import static com.townprotection.GUI.GuiManager.openListGUI;
import static com.townprotection.TownProtection.*;

public class MainGUIListener implements Listener {
    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();
        var slot = event.getSlot();
        var item = event.getCurrentItem();

        if(!playerOpenGUI.containsKey(player)) return;
        var gui = playerOpenGUI.get(player).gui;

        event.setCancelled(true);
        //下のインベントリクリックしてたら無視する
        if(event.getClickedInventory() == player.getInventory()) {
            return;
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        if(gui == GuiManager.GUi.PLAYER_LIST) {
            var data = playerOpenGUI.get(player).listData;
            if(slot < 9*5 && item != null) {
                data.callback.run(slot, event);
            }
            if(item == null) return;
            if(slot == 9 * 5 && item.getType() == Material.FEATHER) {
                data.backCallBack.run(slot, event);
            }
            if(slot == 9*5+4 && item.getType() == Material.REDSTONE_BLOCK) {
                data.interactionCallBack.run(slot, event);
            }

            if(slot == 9*5+7 && item.getType() == Material.FEATHER) {
                //戻る
                listPage.replace(player, listPage.get(player)-1);
                openGUI(player, GuiManager.GUi.PLAYER_LIST);
            }
            if(slot == 9*5+8 && item.getType() == Material.FEATHER) {
                //次のページ
                listPage.replace(player, listPage.get(player)+1);
                openGUI(player, GuiManager.GUi.PLAYER_LIST);
            }
        }
        else if(gui == GuiManager.GUi.MARK_DATA_EDITOR) {
            var guiData = (GUIData) playerOpenGUI.get(player).clone();
            if(slot == 0) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_LIST);
            }
            if(slot == 9+1) {
                String message = TownProtection.message + "新しい土地の名前をチャットに入力して送信してください。";
                CallBackStringByChat.SetName(player, message, (Object s) -> {
                    if(s instanceof String result){
                        if(IsAlreadyExistMarkedName(guiData.targetTownData, result)) {
                            //既に存在します
                            player.sendMessage(TownProtection.message + result + Useful.toColor(" &c&lこの名前はすでに使われているため使用不可能です。"));
                        } else {
                            guiData.targetTownMarkData.displayName = result;
                            player.sendMessage(TownProtection.message + result + " に変更しました。");
                            playerOpenGUI.put(player, guiData);
                            openGUI(player, GuiManager.GUi.MARK_DATA_EDITOR );
                        }
                    }
                });
            }
            if(slot == 9+2) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.CURRENT_MARKED_ALLOW_ACTIONS);
            }
            if(slot == 9+7) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_LIST);
            }
            if(slot == 9+8) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_MANAGER_LIST);
            }
            if(slot == 9*2+8) {
                TeleportSelectorData(player, guiData.targetTownMarkData.selectorData);
            }
        }
        else if(gui == GuiManager.GUi.MARK_DATA_DELETE) {
            if(slot == 9+3) {
                var openData = playerOpenGUI.get(player);
                var townData = openData.targetTownData;
                var targetMarked = openData.targetTownMarkData;
                var townIndex = townMarkData.indexOf(townData);
                townMarkData.get(townIndex).selectorMarkData.remove(targetMarked);
                player.sendMessage(message + "&c&l" + townData.townName + "の、" + targetMarked.displayName + "(土地)を削除しました。");
                player.closeInventory();
            } else if(slot == 9+5) {
                openGUI(player, GuiManager.GUi.MARK_DATA_EDITOR);
            }
        }

        else if(gui == GuiManager.GUi.TOWN_EDITOR) {
            var townData = playerOpenGUI.get(player).targetTownData;
            if(slot == 0) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_LIST);
            }
            else if(slot == 9+1) {
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
            }
            else if(slot == 9+2) {
                //町の土地リストを表示するGUIに移行
                GuiManager.openListGUI(player, GuiManager.ListGUIPreset.TOWN_MARKED_LIST);
            }
            else if(slot == 9+4) {
                if(TownProtection.IsTownAdmin(player, townData)) {
                    GuiManager.openGUI(player, GuiManager.GUi.TOWN_ICON_MODE_SELECT);
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
                                player.sendMessage(TownProtection.message + result + Useful.toColor(" &c&lこの名前はすでに使われているため使用不可能です。"));
                            } else {
                                guiData.targetTownData.townName = result;
                                player.sendMessage(TownProtection.message + result + " に変更しました。");
                                playerOpenGUI.put(player, guiData);
                                openGUI(player, GuiManager.GUi.TOWN_EDITOR);
                            }
                        }
                    });
                }
            }
            else if(slot == 9+7) {
                if(TownProtection.IsTownAdmin(player, townData)) {
                    openListGUI(player, GuiManager.ListGUIPreset.TOWN_SELECT_MAYOR);
                }
            }
            else if(slot == 9+8) {
                openListGUI(player, GuiManager.ListGUIPreset.TOWN_MANAGER_LIST);
            }
            if(slot == 9*2) {
                openGUI(player, GuiManager.GUi.TOWN_DELETE_CONFIRM);
            }
            if(slot == 9*2+8) {
                TeleportSelectorData(player, townData.rangeOfTown);
            }
        }
        else if(gui == GuiManager.GUi.TOWN_EFFECT_EDITOR) {
            var openData = playerOpenGUI.get(player).clone();
            var rawData = playerOpenGUI.get(player);
            var effectData = openData.targetEffectData;
            if(slot == 0) {
                openListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
            } else if(slot == 9*2) {
                rawData.targetTownData.effectList.remove(openData.targetEffectData);
                new CallBackListener().UpdateEffect();
                openListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
                TownProtection.Save();
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
                            openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                        });
                    }

                } else if(slot == 9+2) {
                    if(event.isRightClick()) {
                        showTitleData.setSubTitleEnable(!showTitleData.isSubTitleEnable());
                        playerOpenGUI.put(player, openData);
                        openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                        TownProtection.Save();
                    } else {
                        CallBackStringByChat.SetName(player, message + "表示させる”サブタイトル”をチャットに入力して送信してください", (Object s) -> {
                            String message = (String) s;

                            showTitleData.setSubTitleMessage(message);
                            playerOpenGUI.put(player, openData);
                            openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                        });
                    }
                } else if(slot == 9+6) {
                    if(event.isRightClick()) {
                        showTitleData.setSayMessageEnable(!showTitleData.isSayMessageEnable());
                        playerOpenGUI.put(player, openData);
                        openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                        TownProtection.Save();
                    } else {
                        CallBackStringByChat.SetName(player, message + "表示させる”メッセージ”をチャットに入力して送信してください", (Object s) -> {
                            String message = (String) s;

                            showTitleData.setSayMessage(message);
                            playerOpenGUI.put(player, openData);
                            openGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
                        });
                    }
                }

            }
        }
        else if(gui == GuiManager.GUi.TOWN_CHANGE_MAYOR) {
            if(slot == 9+3) {
                player.sendMessage(message + "市長を" + Bukkit.getOfflinePlayer(playerOpenGUI.get(player).nextMayor) .getName() + "に変更しました" );
                playerOpenGUI.get(player).targetTownData.townMayor = playerOpenGUI.get(player).nextMayor;
                TownProtection.Save();
                player.closeInventory();
            }
            else if(slot == 9+5) {
                openGUI(player, GuiManager.GUi.TOWN_EDITOR);
            }
        }
        else if(gui == GuiManager.GUi.TOWN_DELETE_CONFIRM) {
            if(slot == 9+3) {
                //町を削除
                var targetTown = playerOpenGUI.get(player).targetTownData;
                player.sendMessage(message + targetTown.townName + "を削除しました。");
                townMarkData.remove(targetTown); //削除する
                TownProtection.Save();
                player.closeInventory();
            } else if(slot == 9+5) {
                GuiManager.openGUI(player, GuiManager.GUi.TOWN_EDITOR);
            }
        }
        else if(gui == GuiManager.GUi.TOWN_ICON_MODE_SELECT) {
            if(slot == 0) {
                openGUI(player, GuiManager.GUi.TOWN_EDITOR);
            } else if(slot == 9+3) {
                openListGUI(player, GuiManager.ListGUIPreset.TOWN_ICON_BLOCK_LIST);
            } else if (slot == 9+5) {
                openListGUI(player, GuiManager.ListGUIPreset.TOWN_ICON_PLAYER_INVENTORY_LIST);
            }
        }


    }
}
