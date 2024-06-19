package com.townprotection.Data.GUIData;

import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.PlayerListData.ShowListData;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.GuiManager;

import java.util.UUID;

public class GUIData implements Cloneable{
    public GuiManager.GUi gui;
    public TownData targetTownData;
    public SelectorMarkData targetTownMarkData;
    public AbstractEffect targetEffectData;

    public ShowListData listData = new ShowListData();

    public UUID nextMayor = null;

    @Override
    public GUIData clone(){
        try {
            return (GUIData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
