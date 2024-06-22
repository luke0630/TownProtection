package com.townprotection.Data.PlayerListData;

import com.townprotection.System.RunnableSystem;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShowListData {
    public RunnableSystem.TwoRunnable callback;
    public RunnableSystem.TwoRunnable interactionCallBack;
    public String guiName = "";
    public String interactionName = "&c&l追加する";
    public RunnableSystem.TwoRunnable backCallBack; //戻るボタンを押したときに戻るGUI
    public ShowListDataEnum.showListDataType type = ShowListDataEnum.showListDataType.NONE;
    public List<ItemStack> showItem = new ArrayList<>();
}
