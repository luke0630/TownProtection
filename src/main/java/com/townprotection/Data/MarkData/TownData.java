package com.townprotection.Data.MarkData;

import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class TownData{
    public String townName = "タウンネーム"; //町の名前
    public UUID townMayor; //町長
    public List<String> description = new ArrayList<>(); //町の説明
    public List<UUID> townManager = new ArrayList<>(); //市長よりもえらくない町の権限を持った人

    public String denyMessage = "&c&lこの場所ではその行動は許可されていません。";

    public List<AbstractEffect> effectList = new ArrayList<>();

    public List<SelectorMarkData> selectorMarkData = new ArrayList<>(); //町内の保護
    public List<ActionList.Action> allowActionList = new ArrayList<>(); //町の中で許されているアクション(アクションが起きた場所が土地の範囲だった場合、土地のルールが適用されます)

    public SelectorData rangeOfTown = new SelectorData(); //町の範囲
    public Material townIcon = Material.GRASS_BLOCK;
    public boolean protection = true; //保護を有効にするか

}
