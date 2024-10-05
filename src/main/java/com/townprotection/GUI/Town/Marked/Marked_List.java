package com.townprotection.GUI.Town.Marked;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Selector.Selector;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.Range.ShowRangeWhenEnter.ShowTownAndMarked;
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;
import static org.luke.takoyakiLibrary.TakoUtility.getItem;


public class Marked_List extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    private List<SelectorMarkData> showItem = new ArrayList<>();
    @Override
    public String getGUITitle() {
        return "&8&l町の土地の一覧";
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        clickFilterFunction(inventoryClickEvent, getType());
    }

    @Override
    public GuiManager.ListGUIPreset getType() {
        return GuiManager.ListGUIPreset.TOWN_MARKED_LIST;
    }

    @Override
    public List<ItemStack> getItemList() {
        var townData = playerOpenGUI.get(player).targetTownData;

        if(!townData.selectorMarkData.isEmpty()) {
            controllerItems.put(6, getFilterItem(player));

            List<DataAbstract> dataAbstractList = new ArrayList<>(townData.selectorMarkData);
            var shows = Useful.getDataByFilter(dataAbstractList, player);

            var listData = new ArrayList<ItemStack>();
            for (var targetItem : shows) {
                if(targetItem.getOwner() != null) {
                    var item = getMarkedIcon((SelectorMarkData) targetItem);
                    listData.add(item);
                    showItem.add((SelectorMarkData) targetItem);
                }
            }
            return listData;
        }
        return null;
    }

    @Override
    public ItemStack setCenterItemStack() {
        var townData = playerOpenGUI.get(player).targetTownData;
        var createMarkedMode = townData.createMarkedMode;

        var canCreateMarked = getItem(Material.REDSTONE_BLOCK, "&a&l土地を追加する");
        var cannotCreateMarked = getItem(Material.BARRIER, "&c&l土地追加ができません。");
        TakoUtility.setLore(cannotCreateMarked, List.of(
            "&f現在のこの町の土地の追加が可能な人は以下の通りです。",
            "&a" + createMarkedMode.getString()
        ));

        if(isCanCreateMarked(player, townData)) return canCreateMarked;
        return cannotCreateMarked;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var index = event.getSlot();
            playerOpenGUI.get(player).targetTownMarkData = showItem.get(index);
            playerOpenGUI.get(player).targetData = showItem.get(index);
            getManager().OpenGUI(player, GuiManager.GUi.MARK_DATA_EDITOR);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> {
            if(!isCanCreateMarked(player, playerOpenGUI.get(player).targetTownData)) return;
            var data =  playerSelectData.get(player);
            var townData = playerOpenGUI.get(player).targetTownData;
            SelectorData selectData;
            if(data == null) {
                player.sendMessage(message + toColor("&c&l選択範囲が指定されていません！"));
                return;
            }
            selectData = data.clone();
            var start = selectData.startBlock;
            var end = selectData.endBlock;

            if (start != null && end != null) {
                if (Selector.isRangeInRange(townData.getSelectorData(), selectData)) {
                    var flag = false;
                    var markedCounter = 0;
                    for (var rangeData : townData.selectorMarkData) {
                        if (Selector.overlaps(rangeData.getSelectorData(), selectData)) {
                            if(!flag) {
                                for(var schedulers : Selector.schedulers.get(player)) {
                                    schedulers.cancel();
                                }
                            }
                            if (Selector.getRange(player, rangeData.getSelectorData(), Color.RED)) {
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

                    var selectMarkData = new SelectorMarkData(Material.OAK_LOG ,"無題の土地", getCurrentDate());
                    selectMarkData.setSelectorData(selectData);
                    selectMarkData.setOwner(player.getUniqueId());
                    if (IsAlreadyExistMarkedName(townData, selectMarkData.getName())) {
                        int counter = 1;
                        boolean nameExists;
                        String baseName = selectMarkData.getName();
                        String newName;

                        do {
                            nameExists = false;
                            newName = baseName + "(" + counter + ")";

                            for (var marked : townData.selectorMarkData) {
                                if (marked.getName().equals(newName)) {
                                    nameExists = true;
                                    counter++;
                                    break;
                                }
                            }
                        } while (nameExists);

                        selectMarkData.setName(newName);
                    }

                    player.sendMessage(message + townData.getName() + "の選択したところに、新しい土地を追加しました");
                    townData.selectorMarkData.add(selectMarkData);

                    ShowTownAndMarked(player, townData, false);

                    Save();

                    getManager().OpenListGUI(player, getType());
                } else {
                    player.closeInventory();
                    player.sendMessage(message + toColor("&c&l選択した範囲は町の範囲外です"));
                }
            } else {
                player.closeInventory();
                player.sendMessage(message + toColor("&c&l選択範囲が正しく指定されていません"));
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            TownProtection.getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        };
    }
}
