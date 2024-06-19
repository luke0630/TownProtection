package com.townprotection.GUI.EffectGUIList;

import com.townprotection.Effect.EffectList.ShowTitle;
import com.townprotection.Effect.EffectList.System.AbstractEffect;
import com.townprotection.GUI.EffectGUI;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static com.townprotection.Useful.*;

public class ShowTitleGUI implements EffectGUI.EffectGUISystem {
    @Override
    public void run(AbstractEffect effect, Inventory inv) {
        if(!(effect instanceof ShowTitle data)) return;
        var title = getItem(Material.OAK_SIGN, "&b&l”タイトル”の変更");
        var subTitle = getItem(Material.OAK_HANGING_SIGN, "&b&l”サブタイトル”の変更");
        var message = getItem(Material.BOOK, "&c&lメッセージの変更");

        setLore(title, List.of(
                "&6&l左クリック&c&lしてタイトルの変更",
                "&a&l右クリック&c&lしてタイトル演出を無効",
                //"&c&l右クリックして" + "にする",
                "&f&l-------------------------------------",
                "&a&lステータス: " + getBooleanInJapanese(data.isTitleEnable()),
                "&a&l現在のタイトル: &f&l" + data.getTitleMessage(),
                "&f&l-------------------------------------",
                "&6&l町にプレイヤーが入ったときに表示させるタイトルを設定します"
        ));

        setLore(subTitle, List.of(
                "&6&l左クリック&c&lしてサブタイトルの変更",
                "&a&l右クリック&c&lしてサブタイトル演出を無効",
                //"&c&l右クリックして" + "にする",
                "&f&l-------------------------------------",
                "&a&lステータス: " + getBooleanInJapanese(data.isSubTitleEnable()),
                "&a&l現在のサブタイトル: &f&l" + data.getSubTitleMessage(),
                "&f&l-------------------------------------",
                "&6&l町にプレイヤーが入ったときに表示させるサブタイトルを設定します"
        ));

        setLore(message, List.of(
                "&6&l左クリック&c&lしてメッセージの変更",
                "&a&l右クリック&c&lしてメッセージ演出を無効",
                //"&c&l右クリックして" + "にする",
                "&f&l-------------------------------------",
                "&a&lステータス: " + getBooleanInJapanese(data.isSayMessageEnable()),
                "&a&l現在のメッセージ: &f&l" + data.getSayMessage(),
                "&f&l-------------------------------------",
                "&6&l町にプレイヤーが入ったときに表示させるメッセージを設定します"
        ));

        inv.setItem(9+1, title);
        inv.setItem(9+2, subTitle);
        inv.setItem(9+6, message);
    }
}
