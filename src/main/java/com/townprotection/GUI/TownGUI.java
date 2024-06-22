package com.townprotection.GUI;

import com.townprotection.Data.MainData;
import com.townprotection.TownProtection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static com.townprotection.Useful.*;

public class TownGUI {

    public static Inventory getTownEditor(Player player) {
        var townData = MainData.playerOpenGUI.get(player).targetTownData;
        var guiName = "&c&l" + townData.townName + "&8&lの編集(町)";
        if(!TownProtection.IsTownAdmin(player, townData)) {
            guiName = "&8&l" + townData.townName + "の詳細";
        }
        var inv = getInv(9*3, guiName);

        var townIcon = getItem(townData.townIcon, townData.townName);


        inv.setItem(0, getItem(Material.FEATHER, "&c&l戻る"));

        var townMayorItem = getPlayerHead(townData.townMayor);
        setLore(townMayorItem, List.of(
                "&c&l市長: " + Bukkit.getOfflinePlayer(townData.townMayor)
        ));
        inv.setItem(9+7, townMayorItem);

        var teleportTown = getItem(Material.COMPASS, "テレポートする");
        setLore(teleportTown, List.of(
                "&c&lスペクテイターである必要があります。"
        ));
        inv.setItem(9*2+8, teleportTown);

        if(TownProtection.IsTownAdmin(player, townData)) {
            inv.setItem(9+2, getItem(Material.OAK_LOG, "&c&l土地を管理"));
            inv.setItem(9+1, getItem(Material.SCULK_SENSOR, "&b&l演出を管理"));
            if(player.getUniqueId().toString().equalsIgnoreCase(townData.townMayor.toString())) {
                setLore(townIcon, List.of(
                        "&c&lクリックしてアイコンを変更"
                ));
                var mayor = getItem(Material.PLAYER_HEAD, "&c&l市長: &f&l" + Bukkit.getOfflinePlayer(townData.townMayor).getName());
                setLore(mayor, List.of("&c&lクリックして市長を交代する"));
                inv.setItem(9+7, mayor);
                inv.setItem(9+6, getItem(Material.NAME_TAG, "&c&l名前を変更する"));
                inv.setItem(9+8, getItem(Material.REDSTONE_LAMP, "&c&l町の管理者を追加する"));
                inv.setItem(9*2, getItem(Material.BARRIER, "&c&lこの町を削除する"));
            }
        } else {
            inv.setItem(9+8, getItem(Material.REDSTONE_LAMP, "&c&l町の管理者の一覧"));
             inv.setItem(9+2, getItem(Material.OAK_LOG, "&c&l土地の一覧"));
        }

        inv.setItem(9+4, townIcon);

        return inv;
    }

    public static Inventory getTownEffectEditor(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var effectData = data.targetEffectData;
        var inv = getInv(9*3, "&c&l演出の編集");

        var effectIcon = getItem(effectData.getInfo().getIcon(), effectData.getInfo().getTitle());
        setLore(effectIcon, effectData.getInfo().getDescription());
        var back = getItem(Material.FEATHER, "&c&l戻る");
        var delete = getItem(Material.BARRIER, "&c&l削除する");

        inv.setItem(0, back);
        inv.setItem(9+4, effectIcon);
        inv.setItem(9*2, delete);

        new EffectGUI().SetEffectGUI(effectData, inv);

        return inv;
    }

    public static Inventory getTownChangeMayor(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var inv = getInv(9*3, "&c&l確認画面 - 市長を変更");
        var nextMayorHead = getPlayerHead(data.nextMayor);
        var currentMayorHead = getPlayerHead( data.targetTownData.townMayor );

        var ok = getItem(Material.REDSTONE, "&c&l変更する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&l市名: " + data.targetTownData.townName,
                "&f&lクリックすると、市長交代が行われます。",
                "&c&l" + Bukkit.getOfflinePlayer(data.targetTownData.townMayor).getName() + "&f&l → &c&l" + Bukkit.getOfflinePlayer(data.nextMayor).getName()
        ));

        inv.setItem(3, currentMayorHead);
        inv.setItem(5, nextMayorHead);

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);


        return inv;
    }

    public static Inventory getTownDeleteConfirmGUI(Player player) {
        var data = MainData.playerOpenGUI.get(player);
        var inv = getInv(9*3, "&c&l確認画面 - 町を削除する");

        var townData = data.targetTownData;

        var ok = getItem(Material.REDSTONE, "&c&l町を削除する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&lクリックすると、町が削除されます(ブロックなどに変更はありません。)"
        ));

        inv.setItem(4, getItem(townData.townIcon, townData.townName));

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);


        return inv;
    }

    public static Inventory getTownIconModeSelectGUI(Player player) {
        var data = MainData.playerOpenGUI.get(player);
    var inv = getInv(9*3, "&c&lアイコン変更画面");

        var townData = data.targetTownData;

        var fromInv = getItem(Material.CHEST, "&6&lインベントリのアイテムからアイコンを指定");
        var fromList = getItem(Material.STONE, "&a&lブロック一覧からアイコンを指定");

        inv.setItem(4, getItem(townData.townIcon, townData.townName));

        inv.setItem(0, getItem(Material.FEATHER, "&c&l戻る"));
        inv.setItem(9+3, fromList);
        inv.setItem(9+5, fromInv);

        return inv;
    }
}
