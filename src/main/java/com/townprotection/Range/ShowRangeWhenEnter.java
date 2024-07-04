package com.townprotection.Range;

import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Listener.OriginalListener.PlayerEnterTown;
import com.townprotection.Listener.OriginalListener.PlayerExitTown;
import com.townprotection.Selector.Selector;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemHeldEvent;

import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.Data.MainData.townMarkData;
import static com.townprotection.Range.ShowRange.RemoveShowRange;
import static com.townprotection.Selector.Selector.*;
import static com.townprotection.Useful.HiddenActionBar;
import static com.townprotection.Useful.ShowActionBar;

public class ShowRangeWhenEnter {
    public static void Initialization() {
        CallBackListener.AddCallBack((Object e) -> {
            if(e instanceof PlayerItemHeldEvent event) {
                var player = event.getPlayer();
                if(changeSelectorDataPlayer.containsKey(player)) return;
                if(showModePlayer.contains(player)) return;
                var newItemInHand = player.getInventory().getItem(event.getNewSlot());
                if(newItemInHand != null && Selector.IsSelectorToolWithItem(newItemInHand)) {
                    RemoveShowRange(player);
                    if(playerSelectData.containsKey(player) && playerSelectData.get(player).startBlock != null && playerSelectData.get(player).endBlock != null) {
                        for(var town : townMarkData) {
                            if(overlaps(town.rangeOfTown, playerSelectData.get(player))) {
                                ShowTownAndMarked(player, town, false);
                            }
                        }
                    }

                    var townData = Selector.getTownFromLocation(player.getLocation());
                    if(townData == null) return; //町がなかったら実行しない
                    ShowTownAndMarked(player, townData, true);
                } else if(ShowRange.blocks.containsKey(player)) {
                    //非表示にする
                    if(showModePlayer.contains(player)) return;
                    RemoveShowRange(player);
                }
            } else if(e instanceof PlayerEnterTown event) {
                var player = event.player;
                if(showModePlayer.contains(player)) {
                    ShowTownAndMarked(player, event.townData, true);
                    return;
                }
                if(!Selector.IsSelectorTool(player)) return;
                if(changeSelectorDataPlayer.containsKey(player)) return;
                ShowTownAndMarked(player, event.townData, true);
            } else if(e instanceof PlayerExitTown exit) {
                var player = exit.player;
                if(changeSelectorDataPlayer.containsKey(player)) return;
                HiddenActionBar(player);
                if(showModePlayer.contains(player)) return;
                if(playerSelectData.containsKey(player) && playerSelectData.get(player).endBlock != null && playerSelectData.get(player).startBlock != null && Selector.IsSelectorTool(player) && overlaps(playerSelectData.get(player), exit.previousTownData.rangeOfTown)) return;
                RemoveShowRange(player);
            }
        });
    }

    public static void ShowTownAndMarked(Player player, TownData townData, boolean isShowDescription)  {
        ShowRange.ShowRangeWithBlock(player, townData.rangeOfTown, Material.GOLD_BLOCK, false);
        for(var marked : townData.selectorMarkData) {
            ShowRange.ShowRangeWithBlock(player, marked.selectorData, Material.REDSTONE_BLOCK, false);
        }
        if(isShowDescription) {
            ShowActionBar(player, "&a" + townData.townName + " の範囲を表示中 &f- &6金・町の範囲 &f/ &c赤・土地の範囲");
        }
    }
}
