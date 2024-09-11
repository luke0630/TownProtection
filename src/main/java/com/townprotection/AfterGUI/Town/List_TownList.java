package com.townprotection.AfterGUI.Town;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.GUI.GuiManager;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.GUI.GuiManager.ListGUIPreset.TOWN_LIST;
import static com.townprotection.TownProtection.AddTown;
import static com.townprotection.Useful.*;

public class List_TownList extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<TownData> showItem = new ArrayList<>();

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        clickFilterFunction(inventoryClickEvent, getType());
    }

    @Override
    public GuiManager.ListGUIPreset getType() {
        return TOWN_LIST;
    }

    @Override
    public String getGUITitle() {
        return "&8&l登録されている町の一覧";
    }

    @Override
    public List<ItemStack> getItemList() {
        controllerItems.put(6, getFilterItem(player));

        showItem.clear();
        var items = new ArrayList<ItemStack>();
        List<DataAbstract> showTowns;

        List<DataAbstract> dataAbstractList = new ArrayList<>(MainData.townMarkData);
        showTowns = Useful.getDataByFilter(dataAbstractList, player);

        for (var show : showTowns) {
            var itemStack = getItem(show.getIcon(), show.getName());

            int year = show.getCreationDate().getYear();
            int month = show.getCreationDate().getMonth();
            int day = show.getCreationDate().getDay();

            setLore(itemStack, List.of(
                    "&f-------------------------------------------",
                    "&a&l町長: &f&l" + Bukkit.getOfflinePlayer(show.getOwner()).getName(),
                    "&6&l総土地数: &6" + ((TownData)show).selectorMarkData.size() + "個",
                    "&f&l作成年月日: &f" + year + "年 " + month + "月 " + day + "日"
            ));
            items.add(itemStack);
            showItem.add((TownData) show);
        }
        return items;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return getItem(Material.REDSTONE, "&c&l町を追加する");
    }


    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            var index = event.getSlot();
            playerOpenGUI.put(player, new GUIData());
            var openData = playerOpenGUI.get(player);
            openData.targetTownData = showItem.get(index);

            TownProtection.getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> {
            Player player = (Player) event.getWhoClicked();
            if(!AddTown(player)) {
                player.closeInventory();
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return null;
    }
}
