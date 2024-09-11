package com.townprotection.AfterGUI.Town;

import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.getPlayerHead;
import static com.townprotection.Useful.setLore;

public class List_TownEditor_AddManager extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();
    @Override
    public String getGUITitle() {
        return "&c&l管理者を追加する";
    }

    @Override
    public List<ItemStack> getItemList() {
        var showItemStack = new ArrayList<ItemStack>();
        var managerList = playerOpenGUI.get(player).targetManagerList;
        for (var showPlayer : managerList) {
            var itemStack = getPlayerHead(showPlayer);
            if(IsTownAdmin(player, playerOpenGUI.get(player).targetTownData)) {
                setLore(itemStack, List.of(
                        "&c&l右クリックでこのプレイヤーを管理者から削除する"
                ));
                showItem.add(showPlayer);
                showItemStack.add(itemStack);
            }
        }
        return showItemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return TakoUtility.getItem(Material.REDSTONE_BLOCK, "&c&l町の管理者を追加する");
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var slot = event.getSlot();
            if(event.isRightClick()) {
                var openData = playerOpenGUI.get(player);
                openData.targetTownData.getManager().remove(showItem.get(slot));
                Save();
                getManager().OpenListGUI(player, this.getType());
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_MANAGER_ADD);
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_MANAGER_LIST;
    }
}
