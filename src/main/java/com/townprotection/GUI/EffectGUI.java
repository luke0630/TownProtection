package com.townprotection.GUI;

import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.EffectGUIList.ShowTitleGUI;
import org.bukkit.inventory.Inventory;

public class EffectGUI {
    public interface EffectGUISystem {
        void run(AbstractEffect effect, Inventory inv);
    }
    public static final AbstractEffect[] effectList = {new ShowTitle()};
    final EffectGUISystem[] guiList = {new ShowTitleGUI()};
    public void SetEffectGUI(AbstractEffect effect, Inventory inv) {
        for(var gui : guiList) {
            gui.run(effect, inv);
        }
    }
}
