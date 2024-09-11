package com.townprotection.AfterGUI.Action;

import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.setLore;

public class List_CurrentActions extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<ActionList.Action> showingData = new ArrayList<>();

    @Override
    public String getGUITitle() {
        return "&c&l許可行動リスト";
    }

    @Override
    public List<ItemStack> getItemList() {
        showingData.clear();
        var currentActionList = playerOpenGUI.get(player).targetActionList;
        var itemStack = new ArrayList<ItemStack>();

        for (var action : currentActionList) {
            var guiItem = TakoUtility.getItem(action.getData().displayMaterial, action.getData().name);
            setLore(guiItem, List.of(
                    "&c&l右クリックで削除"
            ));
            showingData.add(action);
            itemStack.add(guiItem);
        }
        return itemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return TakoUtility.getItem(Material.REDSTONE_BLOCK, "&c&l許可行動を追加する");
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            if (event.isRightClick()) {
                var currentActionList = playerOpenGUI.get(player).targetActionList;
                var index = event.getSlot();
                var targetData = showingData.get(index);
                currentActionList.remove(targetData);
                Save();
                getManager().OpenListGUI(player, getType());
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> {
            getManager().OpenListGUI(player, GuiManager.ListGUIPreset.ACTION_LIST);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenGUI(player, playerOpenGUI.get(player).backGUI);
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.ACTION_CURRENT_LIST;
    }
}