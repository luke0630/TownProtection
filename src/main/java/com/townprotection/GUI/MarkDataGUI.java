package com.townprotection.GUI;

import com.townprotection.TownProtection;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static com.townprotection.Useful.*;
import static com.townprotection.Data.MainData.*;

public class MarkDataGUI {
    public static Inventory getMarkDataEditorGUI(Player player) {
        var markData = playerOpenGUI.get(player).targetTownMarkData;
        var inv = getInv(9*3, toColor("&c&l" + markData.displayName + "&8&lの編集"));
        var item = getItem(Material.OAK_LOG, markData.displayName);
        var changeMarkName = getItem(Material.NAME_TAG, "&a&l土地の名前を変更する");

        var owner = getPlayerHead( markData.owner );
        var allowedPlayers = getItem(Material.REDSTONE, "&9&l許可者リスト");
        var manager = getItem(Material.REDSTONE_LAMP, "&6&l管理者リスト");
        var back = getItem(Material.FEATHER, "&c&l戻る");
        var allowList = getItem(Material.LEVER, "&a&l許可行動を選択");

        setLore(manager, List.of(
                "&c&lクリックしてこの土地の管理者を編集",
                "&6&l※管理者は、&c&l土地のルールが適用されず、",
                "&c&l土地の設定が変更可能&f&lです。"
        ));
        setLore(allowedPlayers, List.of(
                "&c&lクリックしてこの土地の許可者を編集",
                "&9&l※許可者には、&c&lルール(保護)が適用されなくなります。"
        ));
        setLore(owner, List.of(
                "&c&lオーナー"
        ));
        setLore(changeMarkName, List.of(
                "&c&l現在の名前: &f&l" + markData.displayName,
                "&c&lクリックして変更する"
        ));
        setLore(allowList, List.of(
                "&c&l許可行動は、&f&l土地内でしてもよい行動を選択できます。",
                "&6&l※チェストを開く...など"
        ));

        if(TownProtection.IsMarkedAdmin(player, markData)) {
            inv.setItem(9+1, changeMarkName);
            inv.setItem(9+2, allowList);
            inv.setItem(9+7, allowedPlayers);
            inv.setItem(9+8, manager);
        }
        inv.setItem(9+4, item);
        inv.setItem(9+6, owner);
        inv.setItem(0, back);
        inv.setItem(9*2, getItem(Material.BARRIER, "&c&l土地を削除する"));

        return inv;
    }

    public static Inventory getMarkDataDeleteGUI(Player player) {
        var targetMarked = playerOpenGUI.get(player).targetTownMarkData;
        var inv = getInv(9*3, toColor("&c&l土地を削除する"));

        var ok = getItem(Material.REDSTONE, "&c&l土地を削除する");
        var no = getItem(Material.BARRIER, "&c&l戻る");

        setLore(ok, List.of(
                "&f&lクリックすると、土地が削除されます(ブロックなどに変更はありません。)"
        ));

        inv.setItem(4, getItem(Material.OAK_LOG, targetMarked.displayName));

        inv.setItem(9+3, ok);
        inv.setItem(9+5, no);

        return inv;
    }


}
