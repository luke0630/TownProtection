package com.townprotection.System;

import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.MarkDataGUI;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import jdk.jfr.Experimental;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.townprotection.TownProtection.Save;
import static com.townprotection.TownProtection.instance;
import static com.townprotection.Useful.stringToUUID;

public class SaveLoad {
    static Path yamlFile = Path.of(File.separator + "Data.yml");
    public void MakeFile() {
        File targetFolder = new File(TownProtection.instance.getDataFolder().getAbsolutePath() + File.separator + "Data");
        File targetYamlFile = new File(targetFolder.getAbsoluteFile().toString() + yamlFile);

        if(!Files.exists(targetFolder.toPath()) || !Files.exists(targetYamlFile.toPath())) {
            try {
                targetFolder.mkdir();
                Files.createFile(targetYamlFile.toPath());
            } catch(Exception ignored) {

            }
        }

        var file = new File(String.valueOf(targetYamlFile));
        instance.configFile = file;
        instance.configData = YamlConfiguration.loadConfiguration(file);
        LoadToConfig();
    }

    public void SaveToFile() {
        try {
            instance.configData.save(instance.configFile); //configからfileに保存する
        } catch (Exception ignored) {
        }
    }


    final String TOWN_SECTION = "Data.TownData.";
    public void SaveToConfig() {
        var data = instance.configData;
        data.set("Data.TownData", null); //初期化s
        int counter = 0;
        for(var townData : MainData.townMarkData) {
            var path = TOWN_SECTION + counter;
            data.set(path + ".name", townData.townName);
            data.set(path + ".mayor", townData.townMayor.toString());

            data.set(path + ".manager", Useful.uuidToString(townData.townManager));
            data.set(path + ".allowAction", townData.allowActionList);
            data.set(path + ".description", townData.description);
            setSelectorData(path + ".range", townData.rangeOfTown);
            setMarkData(path + ".markData.", townData.selectorMarkData);
            setEffectData(path + ".effect.", townData.effectList);
            counter++;
        }
        SaveToFile();
    }

    void setEffectData(String parentPath, List<AbstractEffect> effects) {
        var data = instance.configData;

        for(var effectData : effects) {
            String parentPathBuilder = parentPath + ".";

            if(effectData instanceof ShowTitle titleData) {
                var className = titleData.getClass().getSimpleName();
                data.set(parentPathBuilder + className + ".title", titleData.getTitleMessage());
                data.set(parentPathBuilder + className + ".titleEnable", titleData.isTitleEnable());
                data.set(parentPathBuilder + className + ".subTitle", titleData.getSubTitleMessage());
                data.set(parentPathBuilder + className + ".subTitleEnable", titleData.isSubTitleEnable());
                data.set(parentPathBuilder + className + ".message", titleData.getSayMessage());
                data.set(parentPathBuilder + className + ".messageEnable", titleData.isSayMessageEnable());
            }
        }
    }

    void setMarkData(String parentPath, List<SelectorMarkData> markDataList) {
        var data = instance.configData;

        int counter = 0;

        for(var markData : markDataList) {
            String parentPathBuilder = parentPath + counter;

            data.set(parentPathBuilder + ".owner", markData.owner.toString());
            data.set(parentPathBuilder + ".name", markData.displayName);
            data.set(parentPathBuilder + ".allowedPlayer", Useful.uuidToString(markData.allowedPlayer));
            data.set(parentPathBuilder + ".manager", Useful.uuidToString(markData.manager));

            var actionList = new ArrayList<String>();
            for(var action : markData.allowActionList) {
                actionList.add(action.name());
            }
            data.set(parentPathBuilder + ".allowAction", actionList);
            setSelectorData(parentPathBuilder + ".selectorData", markData.selectorData);
            counter++;
        }
    }

    void setSelectorData(String parentPath, SelectorData selectorData) {
        var data = instance.configData;
        data.set(parentPath + ".start", selectorData.startBlock);
        data.set(parentPath + ".end", selectorData.endBlock);
    }

    void loadEffectData(TownData townData,String parentPath) {
        if(!instance.configData.contains(parentPath)) return;
        for(var className : instance.configData.getConfigurationSection(parentPath).getKeys(false)) {
            var resultPath = parentPath + className + ".";

            if(className.equals(ShowTitle.class.getSimpleName())) {
                ShowTitle effect = new ShowTitle();
                effect.setTitleMessage(instance.configData.getString(resultPath + "title"));
                effect.setSubTitleMessage(instance.configData.getString(resultPath + "subTitle"));
                effect.setSayMessage(instance.configData.getString(resultPath + "message"));

                effect.setTitleEnable(instance.configData.getBoolean(resultPath + "titleEnable"));
                effect.setSubTitleEnable(instance.configData.getBoolean(resultPath + "subTitleEnable"));
                effect.setSayMessageEnable(instance.configData.getBoolean(resultPath + "messageEnable"));
                townData.effectList.add(effect);
            }

        }
    }

    void loadMarkData(TownData townData, String parentPath) {
        if(!instance.configData.contains(parentPath)) return;
        for(var index : instance.configData.getConfigurationSection(parentPath).getKeys(false)) {
            var resultPath = parentPath + index + ".";

            var markData = new SelectorMarkData();
             markData.owner = UUID.fromString(instance.configData.getString(resultPath + "owner"));
             markData.displayName = instance.configData.getString(resultPath + "name");
             markData.allowedPlayer = stringToUUID(instance.configData.getStringList(resultPath + "allowedPlayer"));
             markData.manager = stringToUUID(instance.configData.getStringList(resultPath + "manager"));

             for(var action : instance.configData.getStringList(resultPath + "allowAction")) {
                 markData.allowActionList.add(ActionList.Action.valueOf(action));
             }
             var selectorData = new SelectorData();
             selectorData.startBlock = instance.configData.getLocation(resultPath + "selectorData.start");
            selectorData.endBlock = instance.configData.getLocation(resultPath + "selectorData.end");
            markData.selectorData = selectorData ;
            townData.selectorMarkData.add(markData);
        }
    }

    public void LoadToConfig() {
        if(!instance.configData.contains(TOWN_SECTION)) return;
        for(var path : instance.configData.getConfigurationSection(TOWN_SECTION).getKeys(false)) {
            var resultPath = TOWN_SECTION + path + ".";
            var townData = new TownData();

            townData.townName = instance.configData.getString(resultPath + "name");
            townData.townMayor = UUID.fromString(instance.configData.getString(resultPath + "mayor"));
            townData.townManager = stringToUUID(instance.configData.getStringList(resultPath + "manager"));
            townData.allowActionList = (List<ActionList.Action>) instance.configData.getList(resultPath + "allowAction");
            townData.description =instance.configData.getStringList(resultPath + "description");

            var selectorData = new SelectorData();
            selectorData.startBlock = instance.configData.getLocation(resultPath + "range.start");
            selectorData.endBlock = instance.configData.getLocation(resultPath + "range.end");

            townData.rangeOfTown = selectorData;

            loadMarkData(townData, resultPath + "markData.");
            loadEffectData(townData, resultPath + "effect.");
            MainData.townMarkData.add(townData);
        }
    }
}

