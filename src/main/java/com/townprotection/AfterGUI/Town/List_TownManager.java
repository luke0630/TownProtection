package com.townprotection.AfterGUI.Town;

import com.townprotection.Data.MarkData.TownData;
import com.townprotection.GUI.GuiManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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

public class List_TownManager extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();
    TownData townData = null;
    @Override
    public String getGUITitle() {
        return townData.getName() + "&8&l管理者の編集";
    }

    @Override
    public List<ItemStack> getItemList() {
        var showItemStack = new ArrayList<ItemStack>();
        townData = playerOpenGUI.get(player).targetTownData;
        for (var showPlayer : townData.getManager()) {
            var itemStack = getPlayerHead(showPlayer);
            if(IsTownAdmin(player, townData)) {
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
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_MANAGER_LIST;
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {
        int slot = inventoryClickEvent.getSlot();
        var test = inventoryClickEvent.getWhoClicked();
        test.sendMessage("きたえｊふぁｋｌｄｊふぁｊｄｋｆぁ");
        player.sendMessage(String.valueOf(slot));
        if(slot == 9*5+6) {
            Player player = (Player) inventoryClickEvent.getWhoClicked();
            player.sendMessage("Test");
        }
    }
}
