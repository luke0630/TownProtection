package com.townprotection.GUI.Manager;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
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
import static com.townprotection.GUI.GuiManager.ListGUIPreset.MANAGER_CURRENT_LIST;
import static com.townprotection.TownProtection.*;
import static com.townprotection.Useful.*;

public class List_CurrentManager extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();
    DataAbstract data = null;

    @Override
    public String getGUITitle() {
        String prefix = "";
        if (data instanceof TownData townData) {
            prefix += townData.getName() + "(町)";
        } else if (data instanceof SelectorMarkData markData) {
            prefix += markData.getName() + "(土地)";
        }
        if (IsTopAdmin(player, data)) {
            prefix += "-管理者の編集";
        } else {
            prefix += "-管理者の一覧";
        }
        return "&c&l" + prefix;
    }

    @Override
    public List<ItemStack> getItemList() {
        var showItemStack = new ArrayList<ItemStack>();
        data = playerOpenGUI.get(player).targetData;
        for (var showPlayer : data.getManager()) {
            var itemStack = getPlayerHead(showPlayer);
            var lore = new ArrayList<String>();

            if (IsTopAdmin(player, data)) {
                lore.add("&c&l右クリックでこのプレイヤーを管理者から削除する");
            } else {
                lore.add("&c&lあなたは管理者を編集する権限を持っていません。");
            }
            setLore(itemStack, lore);

            showItem.add(showPlayer);
            showItemStack.add(itemStack);
        }
        return showItemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {
        if (IsTopAdmin(player, data)) {
            return TakoUtility.getItem(Material.REDSTONE_BLOCK, "&c&l町の管理者を追加する");
        }
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var slot = event.getSlot();

            if (!IsTopAdmin(player, data)) {
                player.sendMessage(TakoUtility.toColor("&c&lあなたは管理者の編集の権限がありません。"));
                return;
            }
            if (event.isRightClick()) {
                var openData = playerOpenGUI.get(player);
                openData.targetData.getManager().remove(showItem.get(slot));
                Save();
                getManager().OpenListGUI(player, this.getType());
            }
        };
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        if (IsTopAdmin(player, data)) {
            return (InventoryClickEvent event) -> getManager().OpenListGUI(player, GuiManager.ListGUIPreset.MANAGER_ADD_LIST);
        }
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenGUI(player, playerOpenGUI.get(player).backGUI);
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return MANAGER_CURRENT_LIST;
    }
}
