package com.townprotection.Range;

import com.townprotection.Listener.CallBackListener;
import com.townprotection.Listener.OriginalListener.PlayerEnterTown;
import com.townprotection.Listener.OriginalListener.PlayerExitTown;
import com.townprotection.Selector.Selector;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemHeldEvent;

import static com.townprotection.Range.ShowRange.RemoveShowRange;
import static com.townprotection.TownProtection.message;
import static com.townprotection.Useful.toColor;

public class ShowRangeWhenEnter {
    public static void Initialization() {
        CallBackListener.AddCallBack((Object e) -> {
            if(e instanceof PlayerItemHeldEvent event) {
                var player = event.getPlayer();
                var newItemInHand = player.getInventory().getItem(event.getNewSlot());
                if(newItemInHand != null && Selector.IsSelectorToolWithItem(newItemInHand)) {
                    var townData = Selector.getTownFromLocation(player.getLocation());
                    if(townData == null) return; //町がなかったら実行しない

                    ShowRange.ShowRangeWithBlock(player, townData.rangeOfTown, Material.GOLD_BLOCK);
                    for(var marked : townData.selectorMarkData) {
                        ShowRange.ShowRangeWithBlock(player, marked.selectorData, Material.REDSTONE_BLOCK);
                    }
                    player.sendMessage(message + toColor("町と土地の範囲を表示しています。(" + townData.townName + ")"));
                    player.sendMessage(toColor("&6&l金ブロック&f&lは町の範囲、&c&lレッドストーン&f&lは土地の範囲です。"));
                } else if(ShowRange.blocks.containsKey(player)) {
                    //非表示にする
                    RemoveShowRange(player);
                }
            } else if(e instanceof PlayerEnterTown event) {
                var player = event.player;
                if(!Selector.IsSelectorTool(player)) return;
                ShowRange.ShowRangeWithBlock(event.player, event.townData.rangeOfTown, Material.GOLD_BLOCK);
                for(var marked : event.townData.selectorMarkData) {
                    ShowRange.ShowRangeWithBlock(event.player, marked.selectorData, Material.REDSTONE_BLOCK);
                }
                player.sendMessage(message + toColor("町と土地の範囲を表示しています。(" + event.townData.townName + ")"));
                player.sendMessage(toColor("&6&l金ブロック&f&lは町の範囲、&c&lレッドストーン&f&lは土地の範囲です。"));
            } else if(e instanceof PlayerExitTown exit) {
                var player = exit.player;
                RemoveShowRange(player);
            }
        });
    }
}
