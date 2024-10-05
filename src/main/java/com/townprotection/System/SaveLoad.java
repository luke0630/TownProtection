package com.townprotection.System;

import com.townprotection.Data.ActionData.PlayerInteract;
import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.ActionList;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.townprotection.TownProtection.instance;
import static com.townprotection.Useful.stringToUUID;

public class SaveLoad {
    static Path yamlFile = Path.of(File.separator + "Data.yml");

    public void MakeFile() {
        File targetFolder = new File(TownProtection.instance.getDataFolder().getAbsolutePath() + File.separator + "Data");
        File targetYamlFile = new File(targetFolder.getAbsoluteFile().toString() + yamlFile);

        if (!Files.exists(targetFolder.toPath()) || !Files.exists(targetYamlFile.toPath())) {
            try {
                targetFolder.mkdir();
                Files.createFile(targetYamlFile.toPath());
            } catch (Exception ignored) {

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
        for (var townData : MainData.townMarkData) {
            var path = TOWN_SECTION + counter;
            data.set(path + ".name", townData.getName());
            data.set(path + ".mayor", townData.getOwner().toString());

            data.set(path + ".manager", Useful.uuidToString(townData.getManager()));

            var actionList = new ArrayList<String>();
            for (var action : townData.allowActionList) {
                actionList.add(action.name());
            }
            data.set(path + ".allowAction", actionList);
            if(townData.getPlayerInteractData() != null) {
                Map<String, String> materialTargets = new HashMap<>();
                townData.getPlayerInteractData().getTargets().forEach((material, value) -> {
                    materialTargets.put(material.name(), String.valueOf(value));
                });
                data.set(path + ".allowActionPlayerInteractData", materialTargets);
            }
            data.set(path + ".creationMarkedMode", townData.createMarkedMode.toString());
            data.set(path + ".description", townData.description);
            setSelectorData(path + ".range", townData.getSelectorData());
            setMarkData(path + ".markData.", townData.selectorMarkData);
            setEffectData(path + ".effect.", townData.effectList);
            data.set(path + ".allowedPlayer", Useful.uuidToString(townData.getAllowedPlayer()));

            var datePath = path + ".creationDate.";
            data.set(datePath + "year", townData.getCreationDate().getYear());
            data.set(datePath + "month", townData.getCreationDate().getMonth());
            data.set(datePath + "day", townData.getCreationDate().getDay());

            counter++;
        }
        SaveToFile();
    }

    void setEffectData(String parentPath, List<AbstractEffect> effects) {
        var data = instance.configData;

        for (var effectData : effects) {
            String parentPathBuilder = parentPath + ".";

            if (effectData instanceof ShowTitle titleData) {
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

        for (var markData : markDataList) {
            String parentPathBuilder = parentPath + counter;

            data.set(parentPathBuilder + ".owner", markData.getOwner().toString());
            data.set(parentPathBuilder + ".name", markData.getName());
            data.set(parentPathBuilder + ".allowedPlayer", Useful.uuidToString(markData.getAllowedPlayer()));
            data.set(parentPathBuilder + ".manager", Useful.uuidToString(markData.getManager()));

            var actionList = new ArrayList<String>();
            for (var action : markData.getAllowActionList()) {
                actionList.add(action.name());
            }
            data.set(parentPathBuilder + ".allowAction", actionList);

            if(markData.getPlayerInteractData() != null) {
                Map<String, String> materialTargets = new HashMap<>();
                markData.getPlayerInteractData().getTargets().forEach((material, value) -> {
                    materialTargets.put(material.name(), String.valueOf(value));
                });
                data.set(parentPathBuilder + ".allowActionPlayerInteractData", materialTargets);
            }

            setSelectorData(parentPathBuilder + ".selectorData", markData.getSelectorData());

            var datePath = parentPathBuilder + ".creationDate.";
            data.set(datePath + "year", markData.getCreationDate().getYear());
            data.set(datePath + "month", markData.getCreationDate().getMonth());
            data.set(datePath + "day", markData.getCreationDate().getDay());
            counter++;
        }
    }

    void setSelectorData(String parentPath, SelectorData selectorData) {
        var data = instance.configData;
        data.set(parentPath + ".start", selectorData.startBlock);
        data.set(parentPath + ".end", selectorData.endBlock);
    }

    void loadEffectData(TownData townData, String parentPath) {
        if (!instance.configData.contains(parentPath)) return;
        for (var className : instance.configData.getConfigurationSection(parentPath).getKeys(false)) {
            var resultPath = parentPath + className + ".";

            if (className.equals(ShowTitle.class.getSimpleName())) {
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
        if (!instance.configData.contains(parentPath)) return;
        for (var index : instance.configData.getConfigurationSection(parentPath).getKeys(false)) {
            var resultPath = parentPath + index + ".";

            var datePath = resultPath + ".creationDate.";
            var town_year = instance.configData.getInt(datePath + "year");
            var town_month = instance.configData.getInt(datePath + "month");
            var town_day = instance.configData.getInt(datePath + "day");

            var name = instance.configData.getString(resultPath + "name");
            var markData = new SelectorMarkData(Material.OAK_LOG, name, new DataAbstract.CreationDate(town_year, town_month, town_day));
            markData.setOwner(UUID.fromString(instance.configData.getString(resultPath + "owner")));
            markData.setAllowedPlayer(stringToUUID(instance.configData.getStringList(resultPath + "allowedPlayer")));
            markData.setManager(stringToUUID(instance.configData.getStringList(resultPath + "manager")));

            for (var action : instance.configData.getStringList(resultPath + "allowAction")) {
                markData.allowActionList.add(ActionList.Action.valueOf(action));
            }

            LoadPlayerInteractionData(resultPath, townData);

            var selectorData = new SelectorData();
            selectorData.startBlock = instance.configData.getLocation(resultPath + "selectorData.start");
            selectorData.endBlock = instance.configData.getLocation(resultPath + "selectorData.end");
            markData.setSelectorData(selectorData);
            townData.selectorMarkData.add(markData);
        }
    }

    public void LoadToConfig() {
        if (!instance.configData.contains(TOWN_SECTION)) return;
        for (var path : instance.configData.getConfigurationSection(TOWN_SECTION).getKeys(false)) {
            var resultPath = TOWN_SECTION + path + ".";

            var datePath = resultPath + ".creationDate.";
            var town_year = instance.configData.getInt(datePath + "year");
            var town_month = instance.configData.getInt(datePath + "month");
            var town_day = instance.configData.getInt(datePath + "day");

            var townName = instance.configData.getString(resultPath + "name");
            var townData = new TownData(Material.GRASS_BLOCK, townName, new DataAbstract.CreationDate(town_year, town_month, town_day));

            townData.setOwner(UUID.fromString(instance.configData.getString(resultPath + "mayor")));
            townData.setManager(stringToUUID(instance.configData.getStringList(resultPath + "manager")));

            try {
                townData.createMarkedMode = MainData.CreateMarkedMode.valueOf(instance.configData.getString(resultPath + "creationMarkedMode"));
            } catch (Exception e) {
            }

            townData.setAllowedPlayer(stringToUUID(instance.configData.getStringList(resultPath + "allowedPlayer")));
            for (var action : instance.configData.getStringList(resultPath + "allowAction")) {
                townData.allowActionList.add(ActionList.Action.valueOf(action));
            }

            LoadPlayerInteractionData(resultPath, townData);

            townData.description = instance.configData.getStringList(resultPath + "description");

            var selectorData = new SelectorData();
            selectorData.startBlock = instance.configData.getLocation(resultPath + "range.start");
            selectorData.endBlock = instance.configData.getLocation(resultPath + "range.end");

            townData.setSelectorData(selectorData);

            loadMarkData(townData, resultPath + "markData.");
            loadEffectData(townData, resultPath + "effect.");
            MainData.townMarkData.add(townData);
        }
    }

    public void LoadPlayerInteractionData(String resultPath, DataAbstract data) {
        if (instance.configData.contains(resultPath + "allowActionPlayerInteractData")) {
            var playerInteractData = new PlayerInteract();
            var resultMap = new HashMap<Material, Boolean>();
            for (var interactMaterialPath : instance.configData.getConfigurationSection(resultPath + "allowActionPlayerInteractData").getKeys(false)) {
                var interactResultPath = resultPath + "allowActionPlayerInteractData." + interactMaterialPath;
                String state = instance.configData.getString(interactResultPath);
                Boolean stateBoolean = Boolean.valueOf(state);

                resultMap.put(Material.valueOf(interactMaterialPath), stateBoolean);
            }
            playerInteractData.setTargets(resultMap);
            data.setPlayerInteractData(playerInteractData);
        }
    }
}

