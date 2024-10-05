package com.townprotection.GUI.ChangeOwner;

import com.townprotection.GUI.GuiManager;
import com.townprotection.Data.DataAbstract;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.IsTopAdmin;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.getPlayerHead;
import static com.townprotection.Useful.setLore;

public class SelectNextMayor extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();
    DataAbstract data;
    @Override
    public String getGUITitle() {
        return "&c&lオーナーを変更する";
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
    }

    @Override
    public List<ItemStack> getItemList() {
        showItem.clear();
        var showItemStack = new ArrayList<ItemStack>();
        data = playerOpenGUI.get(player).targetData;
        player.sendMessage(data.getName());
        for (var showPlayer : Bukkit.getOfflinePlayers()) {
            if(data.getOwner().toString().equalsIgnoreCase(showPlayer.getUniqueId().toString())) continue;
            var itemStack = getPlayerHead(showPlayer.getUniqueId());
            if(IsTopAdmin(player, data)) {
                setLore(itemStack, List.of(
                        "&c&lクリックしてオーナー変更確認画面へ移行する"
                ));
            } else {
                setLore(itemStack, List.of(
                        "&c&lこの土地のオーナー"
                ));
            }
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
            if(IsTopAdmin(player, data)) {
                var index = event.getSlot();
                playerOpenGUI.get(player).nextMayor = showItem.get(index);
                getManager().OpenGUI(player, GuiManager.GUi.CHANGE_MAYOR);
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            getManager().OpenGUI(player, playerOpenGUI.get(player).backGUI);
        };
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.SELECT_MAYOR;
    }
}
