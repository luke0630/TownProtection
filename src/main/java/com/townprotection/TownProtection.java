package com.townprotection;

import com.townprotection.CommandRun.MainCommand;
import com.townprotection.CommandRun.MainCommandTabComplete;
import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.GUI.Action.ActionEditor.PlayerInteractGUI;
import com.townprotection.GUI.Action.List_Action;
import com.townprotection.GUI.Action.List_CurrentActions;
import com.townprotection.GUI.AllowedPlayer.List_AddAllowedPlayer;
import com.townprotection.GUI.AllowedPlayer.List_CurrentAllowedPlayer;
import com.townprotection.GUI.ChangeOwner.ConfirmMayor;
import com.townprotection.GUI.ChangeOwner.SelectNextMayor;
import com.townprotection.GUI.GuiManager;
import com.townprotection.GUI.Manager.List_AddManager;
import com.townprotection.GUI.Manager.List_CurrentManager;
import com.townprotection.GUI.SetChangedSelectorDataGUI;
import com.townprotection.GUI.Town.Effect.List_TownEditor_AddEffect;
import com.townprotection.GUI.Town.Effect.List_TownEditor_CurrentEffect;
import com.townprotection.GUI.Town.Effect.TownEditor_EffectEditor;
import com.townprotection.GUI.Town.*;
import com.townprotection.GUI.Town.Marked.Marked_Data_Editor;
import com.townprotection.GUI.Town.Marked.Marked_Delete;
import com.townprotection.GUI.Town.Marked.Marked_List;
import com.townprotection.GUI.Town.Marked.TownEditor_BlockIconList;
import com.townprotection.Listener.BlockBreakListener;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Listener.Listener;
import com.townprotection.PlaceholderAPISystem.TownProtectionExpansion;
import com.townprotection.Range.ShowRange;
import com.townprotection.Range.ShowRangeWhenEnter;
import com.townprotection.Selector.Selector;
import com.townprotection.System.SaveLoad;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.luke.takoyakiLibrary.TakoUtility;
import org.luke.yakisobaGUILib.YakisobaGUIManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.Data.MainData.townMarkData;
import static com.townprotection.Range.ShowRange.RemoveShowRange;
import static com.townprotection.Range.ShowRange.blocks;
import static com.townprotection.Range.ShowRangeWhenEnter.ShowTownAndMarked;
import static com.townprotection.Useful.toColor;

public final class TownProtection extends JavaPlugin {

    public static final String message = toColor("&a&l" + "[TownProtection]&f&l");
    public static TownProtection instance = null;
    public ApiManager apiManager;

    public File configFile;
    public YamlConfiguration configData;


    public static YakisobaGUIManager<GuiManager.GUi, GuiManager.ListGUIPreset> getManager() {
        return manager;
    }

    private static YakisobaGUIManager<GuiManager.GUi, GuiManager.ListGUIPreset> manager;

    @Override
    public void onEnable() {
        manager = new YakisobaGUIManager<>();
        manager.Initialization(this, List.of(
                new TownEditor(),
                new TownEditor_Delete(),
                new List_TownList(),
                new List_TownEditor_CurrentEffect(),
                new List_TownEditor_AddEffect(),
                new TownEditor_EffectEditor(),
                new Marked_List(),
                new Marked_Data_Editor(),
                new Marked_Delete(),
                new List_TownManager(),
                new List_TownEditor_AddManager(),
                new TownEditor_ModeSelect(),
                new TownEditor_BlockIconList(),
                new TownEditor_InventoryIconList(),
                new SetChangedSelectorDataGUI(),
                new List_CurrentActions(),
                new List_Action(),
                new List_AddManager(),
                new List_CurrentManager(),
                new List_CurrentAllowedPlayer(),
                new List_AddAllowedPlayer(),
                new ConfirmMayor(),
                new SelectNextMayor(),
                new PlayerInteractGUI()
        ));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            getLogger().warning("Could not find ProtocolLib! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        new TownProtectionExpansion().register(); //

        instance = this;
        apiManager = new ApiManager();
        var command = getCommand("townprotection");
        Objects.requireNonNull(command).setExecutor(new MainCommand());

        org.bukkit.event.Listener[] listeners = {
                new Listener(),
                new BlockBreakListener(),
                new CallBackListener()
        };

        for(var listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
        getServer().getPluginCommand("townprotection").setTabCompleter(new MainCommandTabComplete());

        new SaveLoad().MakeFile();
        new CallBackListener().Initialization();
        new CallBackListener().UpdateEffect();
        Selector.SetRangeWithToolInitialization();
        ShowRangeWhenEnter.Initialization();
    }

    public static void Save() {
        new SaveLoad().SaveToConfig();
    }

    @Override
    public void onDisable() {
        for(var player : blocks.keySet()) {
            RemoveShowRange(player);
        }
    }


    public static Boolean AddTown(Player player) {
        var data = playerSelectData.get(player);
        if(data == null) {
            player.sendMessage(message + toColor("&c&l選択範囲が選択されていないため追加できませんでした。"));
            return false;
        }
        if(data.startBlock == null) {
            player.sendMessage(message + toColor("&c&l選択範囲の開始地点が選択されていないため追加できませんでした。"));
            return false;
        }
        if(data.endBlock == null) {
            player.sendMessage(message + toColor("&c&l選択範囲の終了地点が選択されていないため追加できませんでした。"));
            return false;
        }

        var overlapsTownList = new ArrayList<SelectorData>();
        for(var otherTown : townMarkData) {
            if(Selector.overlaps(otherTown.getSelectorData(), playerSelectData.get(player))) {
                overlapsTownList.add(otherTown.getSelectorData());
            }
        }
        if(!overlapsTownList.isEmpty()) {
            RemoveShowRange(player);
            for(var selectorData : overlapsTownList) {
                ShowRange.ShowRangeWithBlock(player, selectorData, Material.GOLD_BLOCK, false);
                player.sendActionBar(toColor("&c黄色に表示している町と干渉しているため追加できませんでした。"));
            }
            player.sendMessage(toColor("&c黄色に表示している町と干渉しているため追加できませんでした。"));
            return false;
        }

        var townData = new TownData(Material.GRASS_BLOCK, "無題の町", Useful.getCurrentDate());

        if (TownProtection.IsAlreadyExistTownName(townData.getName())) {
            int counter = 1;
            boolean nameExists;
            String baseName = townData.getName();
            String newName = "";

            do {
                nameExists = false;
                newName = baseName + "(" + counter + ")";

                for (var town : townMarkData) {
                    if (town.getName().equals(newName)) {
                        nameExists = true;
                        counter++;
                        break;
                    }
                }
            } while (nameExists);

            townData.setName(newName);
        }

        townData.setOwner(player.getUniqueId());
        townData.setSelectorData(playerSelectData.get(player).clone()); //必ずクローンを使用する

        townMarkData.add(townData);
        player.sendMessage(message + townData.getName() + " という名前で新たな町を追加しました！この町は保護されています。");

        ShowTownAndMarked(player, townData, false);

        TownProtection.Save();

        player.closeInventory();
        return true;
    }


    public static boolean IsTopAdmin(Player player, DataAbstract dataAbstract) {
        if(player.isOp() || dataAbstract.getOwner().toString().equals(player.getUniqueId().toString())) return true;
        return false;
    }

    public static boolean IsAdmin(Player player, DataAbstract dataAbstract) {
        if(player.isOp() || dataAbstract.getOwner().toString().equals(player.getUniqueId().toString()) || dataAbstract.getManager().contains(player.getUniqueId())) return true;
        return false;
    }

    public static boolean IsTownAdmin(Player player, TownData town) {
        if (player.isOp() || town.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString()) || town.getManager().contains(player.getUniqueId())) return true;
        return false;
    }
    public static boolean IsMarkedAdmin(Player player, SelectorMarkData markData) {
        if(player.isOp() || markData.getOwner().toString().equalsIgnoreCase(player.getUniqueId().toString()) || markData.getManager().contains(player.getUniqueId())) return true;
        return false;
    }

    public static boolean IsAlreadyExistTownName(String name) {
        for(var town : townMarkData) {
            if(town.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public static boolean IsAlreadyExistMarkedName(TownData data, String name) {
        for(var marked : data.selectorMarkData) {
            if(marked.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void TeleportSelectorData(Player player, DataAbstract dataAbstract) {
        SelectorData data = dataAbstract.getSelectorData();

        Block highestBlock = data.startBlock.getWorld().getHighestBlockAt(data.startBlock);
        var resultLocation = highestBlock.getLocation();
        resultLocation.add(0.5, 1, 0.5); //X と Zに0.5ずつ追加することでブロックの中央を求める
        player.teleport(resultLocation);

        String type = "町";
        if(dataAbstract instanceof SelectorMarkData) {
            type = "土地";
        }
        player.sendMessage(TakoUtility.toColor(message + "&fテレポートしました。 : &6" + dataAbstract.getName() + "&f(&c" + type + "&f)"));
    }

}