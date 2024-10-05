package com.townprotection.GUI.Town.Marked;

import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
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

public class TownEditor_BlockIconList extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    static final Integer PAGE_MAX_ITEM = 45;
    List<Material> blocks = new ArrayList<>();

    @Override
    public String getGUITitle() {
        return "&c&lアイコンを選択";
    }

    @Override
    public List<ItemStack> getItemList() {
        var itemStackList = new ArrayList<ItemStack>();
        for (var item : Material.values()) {
            if (item == null) continue;
            if (item.isEmpty()) continue;
            if (item.isAir()) continue;
            if (!item.isItem()) continue;
            if(item == Material.LIGHT_GRAY_STAINED_GLASS_PANE) continue;
            var guiItem = getItem(item, "");
            setLore(guiItem, List.of(
                    "&c&lクリックしてアイコンに設定"
            ));
            blocks.add(item);
            itemStackList.add(guiItem);
        }
        return itemStackList;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var index = event.getSlot();
            var townData = playerOpenGUI.get(player).targetTownData;
            var minIndex = PAGE_MAX_ITEM * getManager().getPlayerCurrentPage().get(player);
            townData.setIcon(blocks.get(minIndex + index));
            Save();
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenGUI(player, GuiManager.GUi.TOWN_ICON_MODE_SELECT);
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_ICON_BLOCK_LIST;
    }
}
