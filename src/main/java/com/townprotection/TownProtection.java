package com.townprotection;

import com.townprotection.CommandRun.MainCommand;
import com.townprotection.CommandRun.MainCommandTabComplete;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Listener.BlockBreakListener;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Listener.GUIListener.MainGUIListener;
import com.townprotection.Listener.Listener;
import com.townprotection.PlaceholderAPISystem.TownProtectionExpansion;
import com.townprotection.Range.ShowRange;
import com.townprotection.Range.ShowRangeWhenEnter;
import com.townprotection.Selector.Selector;
import com.townprotection.System.SaveLoad;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
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

    @Override
    public void onEnable() {

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
                new MainGUIListener(),
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
            if(Selector.overlaps(otherTown.rangeOfTown, playerSelectData.get(player))) {
                overlapsTownList.add(otherTown.rangeOfTown);
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


        var selectMarkData = new SelectorMarkData();
        selectMarkData.selectorData = playerSelectData.get(player).clone(); //ディープコピーを作成

        var townData = new TownData();
        if (TownProtection.IsAlreadyExistTownName(townData.townName)) {
            int counter = 1;
            boolean nameExists;
            String baseName = townData.townName;
            String newName = "";

            do {
                nameExists = false;
                newName = baseName + "(" + counter + ")";

                for (var town : townMarkData) {
                    if (town.townName.equals(newName)) {
                        nameExists = true;
                        counter++;
                        break;
                    }
                }
            } while (nameExists);

            townData.townName = newName;
        }
        townData.townMayor = player.getUniqueId();
        townData.rangeOfTown = playerSelectData.get(player).clone(); //必ずクローンを使用する

        townMarkData.add(townData);
        player.sendMessage(message + townData.townName + " という名前で新たな町を追加しました！");

        ShowTownAndMarked(player, townData, false);

        TownProtection.Save();

        player.closeInventory();
        return true;
    }


    public static boolean IsTownAdmin(Player player, TownData town) {
        if (player.isOp() || town.townMayor.toString().equalsIgnoreCase(player.getUniqueId().toString()) || town.townManager.contains(player.getUniqueId())) return true;
        return false;
    }
    public static boolean IsMarkedAdmin(Player player, SelectorMarkData markData) {
        if(player.isOp() || markData.owner.toString().equalsIgnoreCase(player.getUniqueId().toString()) || markData.manager.contains(player.getUniqueId())) return true;
        return false;
    }

    public static boolean IsAlreadyExistTownName(String name) {
        for(var town : townMarkData) {
            if(town.townName.equals(name)){
                return true;
            }
        }
        return false;
    }
    public static boolean IsAlreadyExistMarkedName(TownData data, String name) {
        for(var marked : data.selectorMarkData) {
            if(marked.displayName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void TeleportSelectorData(Player player, SelectorData data) {
        if(player.getGameMode() == GameMode.SPECTATOR) {
            Selector.getRange(player, data, Color.ORANGE);
            player.sendMessage(message + "テレポートしました。");
            var cloneData = data.clone();
            cloneData.startBlock.setY(player.getY());
            player.teleport(cloneData.startBlock);
        } else {
            player.sendMessage(message + toColor("&c&lスペクテイターモードではないためテレポート出来ませんでした。"));
        }
    }

}