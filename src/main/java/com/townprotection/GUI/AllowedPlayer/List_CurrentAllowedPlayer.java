package com.townprotection.GUI.AllowedPlayer;

import com.townprotection.GUI.GuiManager;
import com.townprotection.Useful;
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
import static com.townprotection.GUI.GuiManager.ListGUIPreset.ALLOWED_ADD_PLAYER_LIST;
import static com.townprotection.GUI.GuiManager.ListGUIPreset.ALLOWED_CURRENT_PLAYER_LIST;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.TownProtection.getManager;

public class List_CurrentAllowedPlayer extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<UUID> showItem = new ArrayList<>();

    @Override
    public String getGUITitle() {
        return playerOpenGUI.get(player).targetData.getName() + "&f-&c許可者の編集";
    }

    @Override
    public List<ItemStack> getItemList() {
        var resultItems = new ArrayList<ItemStack>();
        for(UUID target : playerOpenGUI.get(player).targetData.getAllowedPlayer()) {
            ItemStack playerItem = Useful.getPlayerHead(target);
            TakoUtility.setLore(playerItem, List.of(
                    "&c&l右クリックでプレイヤーを許可者から削除する"
            ));
            resultItems.add(playerItem);
            showItem.add(target);
        }
        return resultItems;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return TakoUtility.getItem(Material.REDSTONE_BLOCK, "&c&l許可者を追加する");
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            if(event.isRightClick()) {
                var slot = event.getSlot();
                var openData = playerOpenGUI.get(player);
                openData.targetData.getAllowedPlayer().remove(showItem.get(slot));
                Save();
                getManager().OpenListGUI(player, this.getType());
            }
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> {
            getManager().OpenListGUI(player, ALLOWED_ADD_PLAYER_LIST);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            getManager().OpenGUI(player, playerOpenGUI.get(player).backGUI);
        };
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return ALLOWED_CURRENT_PLAYER_LIST;
    }
}
