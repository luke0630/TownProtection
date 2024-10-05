package com.townprotection.Data.GUIData;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.PlayerListData.ShowListData;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.GuiManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIData implements Cloneable{
    public GuiManager.GUi gui;
    public List<UUID> targetManagerList = new ArrayList<>(); //市長よりもえらくない町の権限を持った人
    public GuiManager.GUi backGUI = GuiManager.GUi.TOWN_EDITOR;
    public TownData targetTownData;
    public DataAbstract targetData;
    public SelectorMarkData targetTownMarkData;
    public AbstractEffect targetEffectData;
    public ActionList.Action targetActionData;

    public MainData.Filter currentFilter = MainData.Filter.LATEST;

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
