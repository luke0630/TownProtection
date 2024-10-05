package com.townprotection.GUI.AllowedPlayer;

import com.townprotection.GUI.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.TownProtection.getManager;
import static com.townprotection.Useful.getPlayerHead;

public class List_AddAllowedPlayer extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public String getGUITitle() {
        return "&c&l許可者を追加する";
    }

    @Override
    public List<ItemStack> getItemList() {
        var showItemStack = new ArrayList<ItemStack>();
        var data = playerOpenGUI.get(player).targetData;
        for (var rawPlayer : Bukkit.getOfflinePlayers()) {
            if (data.getAllowedPlayer().contains(rawPlayer.getUniqueId()) || data.getOwner().toString().equals(rawPlayer.getUniqueId().toString()))
                continue;

            UUID showPlayer = rawPlayer.getUniqueId();
            var itemStack = getPlayerHead(showPlayer);

            TakoUtility.setLore(itemStack, List.of(
                    "&c&lクリックでこのプレイヤーを許可者として追加する"
            ));

            showItem.add(showPlayer);
            showItemStack.add(itemStack);
        }
        return showItemStack;
    }

    @Override
    public ItemStack setCenterItemStack() {return null;}

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var slot = event.getSlot();
            var openData = playerOpenGUI.get(player);
            openData.targetData.getAllowedPlayer().add(showItem.get(slot));
            Save();
            getManager().OpenListGUI(player, this.getType());
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> getManager().OpenListGUI(player, GuiManager.ListGUIPreset.ALLOWED_CURRENT_PLAYER_LIST);
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.ALLOWED_ADD_PLAYER_LIST;
    }
}