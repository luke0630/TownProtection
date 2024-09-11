package com.townprotection.AfterGUI.Town;

import com.townprotection.GUI.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.getPlayerHead;
import static com.townprotection.Useful.setLore;

public class List_TownEditor_SelectMayor extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();
    @Override
    public String getGUITitle() {
        return "&c&l市長を変更する";
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public List<ItemStack> getItemList() {
        var showItemStack = new ArrayList<ItemStack>();
        var townData = playerOpenGUI.get(player).targetTownData;
        for (var showPlayer : Bukkit.getOfflinePlayers()) {
            if(townData.getOwner().toString().equalsIgnoreCase(showPlayer.getUniqueId().toString())) continue;
            var itemStack = getPlayerHead(showPlayer.getUniqueId());
            setLore(itemStack, List.of(
                    "&c&lクリックして市長にする"
            ));
            showItemStack.add(itemStack);
            showItem.add(showPlayer.getUniqueId());
        }
        return showItemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var index = event.getSlot();
            playerOpenGUI.get(player).nextMayor = showItem.get(index);
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_CHANGE_MAYOR);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        };
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_SELECT_MAYOR;
    }
}
