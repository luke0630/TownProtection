package com.townprotection.AfterGUI.Town.Effect;

import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.GuiManager;
import com.townprotection.TownProtection;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Useful.getItem;
import static com.townprotection.Useful.setLore;

public class List_TownEditor_CurrentEffect extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<AbstractEffect> showItem = new ArrayList<>();
    @Override
    public String getGUITitle() {
        return "&8&l演出の管理";
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_EFFECT;
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }

    @Override
    public List<ItemStack> getItemList() {
        var itemStackList = new ArrayList<ItemStack>();
        var townData = playerOpenGUI.get(player).targetTownData;
        for (var show : townData.effectList) {
            var itemStack = getItem(show.getInfo().getIcon(), show.getInfo().getTitle());
            setLore(itemStack, show.getInfo().getDescription());
            showItem.add(show);
            itemStackList.add(itemStack);
        }
        return itemStackList;
    }

    @Override
    public ItemStack setCenterItemStack() {
        return getItem(Material.REDSTONE_BLOCK, "&c&l追加する");
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickContent() {
        return (InventoryClickEvent event) -> {
            var openData = playerOpenGUI.get(player);
            openData.targetEffectData = showItem.get(event.getSlot());
            TownProtection.getManager().OpenGUI(player, GuiManager.GUi.TOWN_EFFECT_EDITOR);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return (InventoryClickEvent event) -> {
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT_LIST);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            TownProtection.getManager().OpenGUI(player, GuiManager.GUi.TOWN_EDITOR);
        };
    }
}
