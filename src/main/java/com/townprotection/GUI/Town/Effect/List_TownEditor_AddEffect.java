package com.townprotection.GUI.Town.Effect;

import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.GuiManager;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.TownProtection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.luke.yakisobaGUILib.Abstract.ListGUIAbstract;
import org.luke.yakisobaGUILib.CustomRunnable;

import java.util.ArrayList;
import java.util.List;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.Useful.getItem;
import static com.townprotection.Useful.setLore;

public class List_TownEditor_AddEffect extends ListGUIAbstract<GuiManager.ListGUIPreset> {
    List<AbstractEffect> showItem = new ArrayList<>() ;
    @Override
    public String getGUITitle() {
        return "&c&l演出の追加";
    }

    @Override
    public Enum<GuiManager.ListGUIPreset> getType() {
        return GuiManager.ListGUIPreset.TOWN_EFFECT_LIST;
    }

    @Override
    public List<ItemStack> getItemList() {
        var itemStackList = new ArrayList<ItemStack>();
        var townData = playerOpenGUI.get(player).targetTownData;
        for (var show : EffectGUI.effectList) {
            if(townData.effectList.contains(show)) continue;
            var itemStack = getItem(show.getInfo().getIcon(), show.getInfo().getTitle());

            var lore = new ArrayList<>(show.getInfo().getDescription());
            lore.add("&c&lクリックして追加");
            setLore(itemStack, lore);
            showItem.add(show);
            itemStackList.add(itemStack);
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
            var townData = playerOpenGUI.get(player).targetTownData;
            townData.effectList.add(showItem.get(event.getSlot()));
            new CallBackListener().UpdateEffect();
            Save();
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT_LIST);
        };
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickCenter() {
        return null;
    }

    @Override
    public CustomRunnable.InventoryRunnable whenClickBack() {
        return (InventoryClickEvent event) -> {
            TownProtection.getManager().OpenListGUI(player, GuiManager.ListGUIPreset.TOWN_EFFECT);
        };
    }

    @Override
    public void customInventoryClickEvent(InventoryClickEvent inventoryClickEvent) {

    }
}
