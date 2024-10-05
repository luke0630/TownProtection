package com.townprotection.GUI.Action;

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

public class List_CurrentActions extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<ActionList.Action> showingData = new ArrayList<>();

    @Override
    public String getGUITitle() {
        return "&c&l許可行動の編集";
    }

    @Override
    public List<ItemStack> getItemList() {
        showingData.clear();
        var currentActionList = playerOpenGUI.get(player).targetData.getAllowActionList();
        var itemStack = new ArrayList<ItemStack>();

        for (var action : currentActionList) {
            var item = ActionList.getActionItem(action);

            List<String> currentLore = new ArrayList<>();
            if(item.getLore() != null) {
                currentLore.addAll(item.getLore());
            }
            currentLore.addAll(List.of(
                    "&2&l-------------------------",
                    "&b右クリックで削除"
            ));
            TakoUtility.setLore(item, currentLore);
            showingData.add(action);
            itemStack.add(item);
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
            var index = event.getSlot();
            var targetData = showingData.get(index);

            if(event.isLeftClick()) {
                if(targetData == ActionList.Action.PLAYER_INTERACT) {
                    getManager().OpenListGUI(player, GuiManager.ListGUIPreset.PlayerInteractGUI);
                }
            }

            if (event.isRightClick()) {
                var currentActionList = playerOpenGUI.get(player).targetData.getAllowActionList();
                currentActionList.remove(targetData);
                playerOpenGUI.get(player).targetData.setPlayerInteractData(null);
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