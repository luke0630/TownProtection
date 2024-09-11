package com.townprotection.AfterGUI.Action;

import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.GUI.GuiManager;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.getItem;
import static com.townprotection.Useful.setLore;

public class List_Action extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<ActionList.Action> actionList = new ArrayList<>();
    @Override
    public String getGUITitle() {
        return "&c&l許可行動を追加する";
    }

    @Override
    public List<ItemStack> getItemList() {
        actionList.clear();
        var targetActionList = playerOpenGUI.get(player).targetActionList;
        var itemStack = new ArrayList<ItemStack>();

        for (var action : ActionList.Action.values()) {
            if (targetActionList.contains(action)) continue; //すでに含まれている場合はスキップ
            var item = getItem(action.getData().displayMaterial, action.getData().name);
            setLore(item, List.of(
                    "&c&lクリックして追加"
            ));
            itemStack.add(item);
            actionList.add(action);
        }
        return itemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var actionlist = playerOpenGUI.get(player).targetActionList;
            var index = event.getSlot();
            actionlist.add(actionList.get(index));
            Save();
            getManager().OpenListGUI(player, getType());
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenListGUI(player, GuiManager.ListGUIPreset.ACTION_CURRENT_LIST);
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.ACTION_LIST;
    }
}
