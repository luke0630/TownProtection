package com.townprotection.GUI;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.PlayerListData.ShowListData;
import com.townprotection.Data.PlayerListData.ShowListDataEnum;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Selector.Selector;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Data.MainData.*;
import static com.townprotection.GUI.MarkDataGUI.getMarkDataDeleteGUI;
import static com.townprotection.GUI.MarkDataGUI.getMarkDataEditorGUI;
import static com.townprotection.GUI.ShowListGUI.PAGE_MAX_ITEM;
import static com.townprotection.GUI.ShowListGUI.getListGUI;
import static com.townprotection.GUI.TownGUI.*;
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;

public class GuiManager {
    public enum GUi {
        PLAYER_LIST,
        MARK_DATA_EDITOR,
        MARK_DATA_DELETE,
        TOWN_EDITOR,
        TOWN_EFFECT_EDITOR,
        TOWN_CHANGE_MAYOR,
        TOWN_DELETE_CONFIRM,
        TOWN_ICON_MODE_SELECT,
    }

    public enum ListGUIPreset {
        TOWN_LIST,
        TOWN_EFFECT,
        TOWN_EFFECT_LIST,
        TOWN_SELECT_MAYOR,
        TOWN_MANAGER_LIST,
        TOWN_MANAGER_ADD,
        TOWN_MARKED_LIST,
        TOWN_MARKED_MANAGER_LIST,
        TOWN_MARKED_MANAGER_ADD,
        TOWN_MARKED_ALLOWED_PLAYER_LIST,
        TOWN_MARKED_ALLOWED_PLAYER_ADD_LIST,
        TOWN_ICON_BLOCK_LIST,
        TOWN_ICON_PLAYER_INVENTORY_LIST,
        CURRENT_MARKED_ALLOW_ACTIONS,
        ACTION_LIST
    }

    public static Inventory getGUI(Player player) {
        var gui = playerOpenGUI.get(player).gui;
        return switch (gui) {
            case PLAYER_LIST -> getListGUI(player);
            case MARK_DATA_EDITOR -> getMarkDataEditorGUI(player);
            case MARK_DATA_DELETE -> getMarkDataDeleteGUI(player);

            case TOWN_EDITOR -> getTownEditor(player);
            case TOWN_EFFECT_EDITOR -> getTownEffectEditor(player);
            case TOWN_CHANGE_MAYOR -> getTownChangeMayor(player);
            case TOWN_DELETE_CONFIRM -> getTownDeleteConfirmGUI(player);
            case TOWN_ICON_MODE_SELECT -> getTownIconModeSelectGUI(player);
        };
    }

    private static void getListGUIPreset(Player player, ListGUIPreset guiPreset) {
        var listData = playerOpenGUI.get(player).listData;
        var markData = playerOpenGUI.get(player).targetTownMarkData;
        var townData = playerOpenGUI.get(player).targetTownData;
        listData.showItem.clear();
        switch (guiPreset) {
            case TOWN_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_INTERACTION;
                listData.guiName = "&8&l登録されている町の一覧";

                var showItem = new ArrayList<TownData>();
                for (var show : townMarkData) {
                    var itemStack = getItem(show.townIcon, show.townName);
                    setLore(itemStack, List.of(
                            "&a&l町長: &f&l" + Bukkit.getOfflinePlayer(show.townMayor).getName()
                    ));
                    showItem.add(show);
                    listData.showItem.add(itemStack);
                }

                listData.interactionCallBack = (Object s, Object e) -> {
                    if(!AddTown(player)) {
                        player.closeInventory();
                    }
                };

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        var openData = playerOpenGUI.get(player);
                        openData.targetTownData = showItem.get(index);
                        GuiManager.openGUI(player, GUi.TOWN_EDITOR);
                    }
                };
            }
            case TOWN_EFFECT -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                listData.guiName = "&8&l演出の管理";

                var showItem = new ArrayList<AbstractEffect>();
                for (var show : townData.effectList) {
                    var itemStack = getItem(show.getInfo().getIcon(), show.getInfo().getTitle());
                    setLore(itemStack, show.getInfo().getDescription());
                    showItem.add(show);
                    listData.showItem.add(itemStack);
                }

                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);

                listData.interactionCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_EFFECT_LIST);

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        var openData = playerOpenGUI.get(player);
                        openData.targetEffectData = showItem.get(index);

                        GuiManager.openGUI(player, GUi.TOWN_EFFECT_EDITOR);
                    }
                };
            }
            case TOWN_EFFECT_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&l演出の追加";

                var showItem = new ArrayList<AbstractEffect>();
                for (var show : EffectGUI.effectList) {
                    if(townData.effectList.contains(show)) continue;
                    var itemStack = getItem(show.getInfo().getIcon(), show.getInfo().getTitle());

                    var lore = new ArrayList<>(show.getInfo().getDescription());
                    lore.add("&c&lクリックして追加");
                    setLore(itemStack, lore);
                    showItem.add(show);
                    listData.showItem.add(itemStack);
                }

                listData.backCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_EFFECT);

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        townData.effectList.add(showItem.get(index));
                        new CallBackListener().UpdateEffect();
                        openListGUI(player, ListGUIPreset.TOWN_EFFECT_LIST);
                        Save();
                    }
                };
            }
            case TOWN_SELECT_MAYOR -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&l市長を変更する";

                var showItem = new ArrayList<UUID>();
                for (var showPlayer : Bukkit.getOfflinePlayers()) {
                    if(townData.townMayor.toString().equalsIgnoreCase(showPlayer.getUniqueId().toString())) continue;
                    var itemStack = getPlayerHead(showPlayer.getUniqueId());
                    setLore(itemStack, List.of(
                            "&c&lクリックして市長にする"
                    ));
                    showItem.add(showPlayer.getUniqueId());
                    listData.showItem.add(itemStack);
                }

                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        playerOpenGUI.get(player).nextMayor = showItem.get(index);
                        openGUI(player, GUi.TOWN_CHANGE_MAYOR);
                    }
                };
            }
            case TOWN_MANAGER_LIST -> {
                var showItem = new ArrayList<UUID>();
                for (var showPlayer : townData.townManager) {
                    var itemStack = getPlayerHead(showPlayer);
                    if(IsTownAdmin(player, townData)) {
                        setLore(itemStack, List.of(
                                "&c&l右クリックでこのプレイヤーを管理者から削除する"
                        ));
                        showItem.add(showPlayer);
                    }
                    listData.showItem.add(itemStack);
                }

                if(IsTownAdmin(player, townData)) {
                    listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                    listData.guiName = townData.townName + "&8&l管理者の編集";
                    listData.interactionName = "&c&l町の管理者を追加する";

                    listData.callback = (Object s, Object e) -> {
                        if (s instanceof Integer index && e instanceof InventoryClickEvent event) {
                            if(event.isRightClick()) {
                                var openData = playerOpenGUI.get(player);
                                openData.targetTownData.townManager.remove(showItem.get(index));
                                Save();
                                openListGUI(player, ListGUIPreset.TOWN_MANAGER_LIST);
                            }
                        }
                    };
                    listData.interactionCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_MANAGER_ADD);
                } else {
                    listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                    listData.guiName = townData.townName + "&8&lの管理者リスト";
                }
                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);
            }
            case TOWN_MANAGER_ADD -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = townData.townName + "&c&lの管理者を追加する";

                var showItem = new ArrayList<UUID>();
                for (var showPlayer : Bukkit.getOfflinePlayers()) {
                    if(townData.townMayor == showPlayer.getUniqueId()) continue;
                    if(townData.townManager.contains(showPlayer.getUniqueId())) continue;

                    var itemStack = getPlayerHead(showPlayer.getUniqueId());
                    setLore(itemStack, List.of(
                            "&c&lクリックして管理者に追加"
                    ));
                    showItem.add(showPlayer.getUniqueId());
                    listData.showItem.add(itemStack);
                }

                listData.backCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_MANAGER_LIST);

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        var openData = playerOpenGUI.get(player);
                        openData.targetTownData.townManager.add(showItem.get(index));
                        Save();
                        openListGUI(player, ListGUIPreset.TOWN_MANAGER_ADD);
                    }
                };
            }

            case TOWN_MARKED_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                if(!IsTownAdmin(player, townData)) {
                    listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                }
                listData.guiName = "&8&l町の土地の一覧";
                listData.interactionName = "&a&l土地を追加する";
                listData.interactionCallBack = (Object s, Object e) -> {
                    var data = playerSelectData.get(player);
                    SelectorData selectData;
                    if(data == null) {
                        player.sendMessage(message + toColor("&c&l選択範囲が指定されていません！"));
                        return;
                    }
                    selectData = data.clone();
                    var start = selectData.startBlock;
                    var end = selectData.endBlock;


                    if (start != null && end != null) {
                        if (Selector.isRangeInRange(townData.rangeOfTown, selectData)) {
                            var flag = false;
                            var markedCounter = 0;
                            for (var rangeData : townData.selectorMarkData) {
                                if (Selector.overlaps(rangeData.selectorData, selectData)) {
                                    if(!flag) {
                                        for(var schedulers : Selector.schedulers.get(player)) {
                                            schedulers.cancel();
                                        }
                                    }
                                    if (Selector.getRange(player, rangeData.selectorData, Color.RED)) {
                                        flag  = true;
                                        markedCounter++;
                                    } else {
                                        player.sendMessage(message + toColor("&c&l他の土地と重なっているため追加できませんでした。"));
                                    }
                                    player.closeInventory();
                                }
                            }
                            if(flag) {
                                player.sendMessage(message + toColor("&c&l現在赤色に表示させている土地と重なっているため追加できませんでした。&6&l重なっている土地: " + markedCounter + "個"));
                                return;
                            }

                            var selectMarkData = new SelectorMarkData();
                            selectMarkData.selectorData = selectData;
                            selectMarkData.owner = player.getUniqueId();
                            if (IsAlreadyExistMarkedName(townData, selectMarkData.displayName)) {
                                int counter = 1;
                                boolean nameExists;
                                String baseName = selectMarkData.displayName;
                                String newName;

                                do {
                                    nameExists = false;
                                    newName = baseName + "(" + counter + ")";

                                    for (var marked : townData.selectorMarkData) {
                                        if (marked.displayName.equals(newName)) {
                                            nameExists = true;
                                            counter++;
                                            break;
                                        }
                                    }
                                } while (nameExists);

                                selectMarkData.displayName = newName;
                            }

                            player.sendMessage(message + townData.townName + "の選択したところに、新しい土地を追加しました。");
                            townData.selectorMarkData.add(selectMarkData);

                            Save();

                            GuiManager.openListGUI(player, ListGUIPreset.TOWN_MARKED_LIST);
                        } else {
                            player.closeInventory();
                            player.sendMessage(message + toColor("&c&l選択した範囲は町の範囲外です。"));
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(message + toColor("&c&l選択範囲が正しく指定されていません。"));
                    }
                };
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        playerOpenGUI.get(player).targetTownMarkData = townData.selectorMarkData.get(index);
                        openGUI(player, GUi.MARK_DATA_EDITOR);
                    }
                };
                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);

                if(townData.selectorMarkData.isEmpty()) return;
                for (var targetItem : townData.selectorMarkData) {
                    var item = getItem(Material.OAK_LOG, targetItem.displayName);


                    if(targetItem.owner != null) {
                        var playeraaa = Bukkit.getOfflinePlayer(targetItem.owner);
                        setLore(item, List.of(
                                "&c&lオーナー: &f&l" + playeraaa.getName()
                        ));
                        listData.showItem.add(item);
                    }
                }
            }
            case TOWN_MARKED_MANAGER_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                listData.guiName = "&8&l管理者を編集する";
                listData.interactionName = "&c&l管理者を追加する";

                for (var pl : markData.manager) {
                    var guiItem = getPlayerHead(pl);
                    setLore(guiItem, List.of(
                            "&c&l右クリックで削除"
                    ));
                    listData.showItem.add(guiItem);
                }
                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.MARK_DATA_EDITOR);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent event) {
                        if (event.isRightClick()) {
                            markData.manager.remove((int) index);
                            Save();
                            openListGUI(player, ListGUIPreset.TOWN_MARKED_MANAGER_LIST);
                        }
                    }
                };
                listData.interactionCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_MARKED_MANAGER_ADD);
            }
            case TOWN_MARKED_MANAGER_ADD -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&l管理者にしたい人をクリック";

                var players = new ArrayList<UUID>();
                for (var pl : Bukkit.getOfflinePlayers()) {
                    if (markData.owner.toString().equalsIgnoreCase(pl.getUniqueId().toString())) continue; //オーナー省く
                    if (markData.manager.contains(pl.getUniqueId())) continue; //権限者を省く

                    players.add(pl.getUniqueId());
                    var guiItem = getPlayerHead(pl.getUniqueId());
                    setLore(guiItem, List.of(
                            "&c&lクリックして追加"
                    ));
                    listData.showItem.add(guiItem);
                }
                listData.backCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_MARKED_MANAGER_LIST);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent) {
                        markData.manager.add(players.get(index));
                        playerOpenGUI.get(player).listData.showItem.clear();
                        for (var pl : Bukkit.getOfflinePlayers()) {
                            if (markData.owner == pl.getUniqueId()) continue; //オーナー省く
                            if (markData.manager.contains(pl.getUniqueId())) continue; //権限者を省く

                            players.add(pl.getUniqueId());
                            var guiItem = getPlayerHead(pl.getUniqueId());
                            setLore(guiItem, List.of(
                                    "&c&lクリックして追加"
                            ));
                            listData.showItem.add(guiItem);
                        }
                        Save();
                        GuiManager.openGUI(player, GUi.PLAYER_LIST);
                    }
                };

            }
            case TOWN_MARKED_ALLOWED_PLAYER_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                listData.guiName = "&8&l許可者を編集する";
                listData.interactionName = "&c&l許可者を追加する";

                for (var pl : markData.allowedPlayer) {
                    var guiItem = getPlayerHead(pl);
                    setLore(guiItem, List.of(
                            "&c&l右クリックで削除"
                    ));
                    listData.showItem.add(guiItem);
                }
                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.MARK_DATA_EDITOR);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index && e instanceof InventoryClickEvent event) {
                        if (event.isRightClick()) {
                            markData.allowedPlayer.remove((int) index);
                            Save();
                            openListGUI(player, ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_LIST);
                        }
                    }
                };
                listData.interactionCallBack = (Object s, Object e) -> {
                    openListGUI(player, ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_ADD_LIST);
                };
            }
            case TOWN_MARKED_ALLOWED_PLAYER_ADD_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&l許可したい人を追加する";
                listData.interactionName = "&c&l許可者を追加する";

                var playerList = new ArrayList<UUID>();
                for (var pl : Bukkit.getOfflinePlayers()) {
                    if (markData.owner.toString().equalsIgnoreCase(pl.getUniqueId().toString())) continue; //オーナー省く
                    if (markData.manager.contains(pl.getUniqueId())) continue; //権限者を省く
                    if (markData.allowedPlayer.contains(pl.getUniqueId())) continue; //許可者を省く
                    var guiItem = getPlayerHead(pl.getUniqueId());
                    setLore(guiItem, List.of(
                            "&c&lクリックして追加"
                    ));
                    playerList.add(pl.getUniqueId());
                    listData.showItem.add(guiItem);
                }

                listData.backCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_LIST);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index) {
                        markData.allowedPlayer.add(playerList.get(index));
                        Save();
                        openListGUI(player, ListGUIPreset.TOWN_MARKED_ALLOWED_PLAYER_ADD_LIST);
                    }
                };
            }
            case TOWN_ICON_BLOCK_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&lアイコンを選択";

                var blocks = new ArrayList<Material>();
                for (var item : Material.values()) {
                    if(item == null) continue;
                    if(item.isEmpty()) continue;
                    if(item.isAir()) continue;
                    var guiItem = getItem(item, "");
                    setLore(guiItem, List.of(
                            "&c&lクリックしてアイコンに設定"
                    ));
                    blocks.add(item);
                    listData.showItem.add(guiItem);
                }

                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index) {
                        var minIndex = PAGE_MAX_ITEM* listPage.get(player);
                        townData.townIcon = blocks.get(minIndex + index);
                        Save();
                        openGUI(player, GUi.TOWN_EDITOR);
                    }
                };
            }
            case TOWN_ICON_PLAYER_INVENTORY_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&lアイコンにしたいアイテムを選択";


                var blocks = new ArrayList<Material>();
                for (var item : player.getInventory().getStorageContents()) {
                    if(item == null) continue;
                    var guiItem = new ItemStack(item.getType());
                    setLore(guiItem, List.of(
                            "&c&lクリックしてアイコンに設定"
                    ));
                    blocks.add(item.getType());
                    listData.showItem.add(guiItem);
                }

                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.TOWN_EDITOR);
                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index) {
                        townData.townIcon = blocks.get(index);
                        Save();
                        openGUI(player, GUi.TOWN_EDITOR);
                    }
                };
            }


            case CURRENT_MARKED_ALLOW_ACTIONS -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK_INTERACTION;
                listData.guiName = "&c&l許可行動リスト";
                listData.interactionName = "&c&l許可行動を追加する";

                var currentActionList = markData.allowActionList;
                for (var action : currentActionList) {
                    var guiItem = getItem(action.getData().displayMaterial, action.getData().name);
                    setLore(guiItem, List.of(
                            "&c&l右クリックで削除"
                    ));
                    listData.showItem.add(guiItem);
                }

                listData.backCallBack = (Object s, Object e) -> openGUI(player, GUi.MARK_DATA_EDITOR);
                listData.interactionCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.ACTION_LIST);
                listData.callback = (Object s, Object e) -> {
                    if (e instanceof InventoryClickEvent event) {
                        if (event.isRightClick() && s instanceof Integer index) {
                            markData.allowActionList.remove((int) index);
                            Save();
                            openListGUI(player, ListGUIPreset.CURRENT_MARKED_ALLOW_ACTIONS);
                        }
                    }
                };
            }
            case ACTION_LIST -> {
                listData.type = ShowListDataEnum.showListDataType.BOTTOM_BACK;
                listData.guiName = "&c&l許可行動を追加する";

                var actionList = new ArrayList<ActionList.Action>();
                for (var action : ActionList.Action.values()) {
                    if (markData.allowActionList.contains(action)) continue; //すでに含まれている場合はスキップ
                    var item = getItem(action.getData().displayMaterial, action.getData().name);
                    setLore(item, List.of(
                            "&c&lクリックして追加"
                    ));
                    listData.showItem.add(item);
                    actionList.add(action);
                }

                listData.callback = (Object s, Object e) -> {
                    if (s instanceof Integer index) {
                        markData.allowActionList.add(actionList.get(index));
                        Save();
                        openListGUI(player, ListGUIPreset.ACTION_LIST);
                    }
                };
                listData.backCallBack = (Object s, Object e) -> openListGUI(player, ListGUIPreset.CURRENT_MARKED_ALLOW_ACTIONS);
            }

            default -> throw new IllegalStateException("Unexpected value: " + guiPreset);
        }
    }

    public static void openGUI(Player player, GUi gui) {
        var data = new GUIData();
        if (playerOpenGUI.containsKey(player)) {
            data = playerOpenGUI.get(player);
        }
        data.gui = gui;

        //CloseInventoryEventでremoveされてしまうためサンドイッチしてデータが消されるの防ぐ
        playerOpenGUI.put(player, data);

        //playerOpenGUIに代入した後に必ず実行する(getGUIメソッドはplayerOpenGUIから値を取得するため)
        var openGUI = getGUI(player);
        if (openGUI == null) {
            player.sendMessage(message + "&c&lエラーが発生しました。");
            return;
        }
        player.openInventory(openGUI); //GUIを開く
        playerOpenGUI.put(player, data);
    }

    public static void openListGUI(Player player, ListGUIPreset guiPreset) {
        listPage.replace(player, 0);
        playerOpenGUI.get(player).listData = new ShowListData();
        playerOpenGUI.get(player).listData.showItem.clear();
        getListGUIPreset(player, guiPreset);
        openGUI(player, GUi.PLAYER_LIST);
    }


}
