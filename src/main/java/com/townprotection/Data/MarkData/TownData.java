package com.townprotection.Data.MarkData;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MainData;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class TownData extends DataAbstract {
    public List<String> description = new ArrayList<>(); //町の説明
    public String denyMessage = "&c&lこの場所ではその行動は許可されていません。";
    public List<AbstractEffect> effectList = new ArrayList<>();
    public MainData.CreateMarkedMode createMarkedMode = MainData.CreateMarkedMode.ALL;
    public List<SelectorMarkData> selectorMarkData = new ArrayList<>(); //町内の保護
    public boolean protection = true; //保護を有効にするか

    public TownData(Material icon, String name, CreationDate date) {
        super(icon, name, date);
    }
}
