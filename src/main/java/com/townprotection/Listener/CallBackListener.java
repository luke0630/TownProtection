package com.townprotection.Listener;

import com.townprotection.Data.MainData;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Listener.OriginalListener.PlayerEnterTown;
import com.townprotection.Listener.OriginalListener.PlayerExitTown;
import com.townprotection.Selector.Selector;
import com.townprotection.System.RunnableSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallBackListener implements Listener {
    private static List<RunnableSystem.Runnable> listenerCallBack = new ArrayList<>();

    public static void AddCallBack(RunnableSystem.Runnable callback) {
        if(!listenerCallBack.contains(callback)) {
            listenerCallBack.add(callback);
        }
    }
    public static void DeleteCallBack(RunnableSystem.Runnable callback) {
        listenerCallBack.remove(callback);
    }

    public void Initialization() {
        OnPlayerEnterTown();
        OnPlayerExitTown();
    }

    public void UpdateEffect() {
        listenerCallBack.clear(); //一度クリアする
        for(var data : MainData.townMarkData) {
            for(var effect : data.effectList) {
                effect.run();
            }
        }

        Initialization();
    }

    void run(Object event) {
        for(var callBack : listenerCallBack) {
            callBack.run(event);
        }
    }

    Map<Player, TownData> playerEnterTown = new HashMap<>();
    public void OnPlayerEnterTown() {
        AddCallBack((Object e) -> {
            if(e instanceof PlayerMoveEvent moveEvent) {
                var event = new PlayerEnterTown();
                var loc = moveEvent.getTo();
                var town = Selector.getTownFromLocation(loc);
                SelectorMarkData marked = null;
                if(town != null) {
                    marked = Selector.getMarkDataFromLocation(town, loc);
                }

                event.townData = town;
                event.markData = marked;
                event.player = moveEvent.getPlayer();

                if(event.townData != null) {
                    if(!playerEnterTown.containsKey(event.player) || town != playerEnterTown.get(event.player)) {
                        playerEnterTown.put(event.player, town);
                        run(event);
                    }
                }
            }
        });
    }
    public void OnPlayerExitTown() {
        AddCallBack((Object e) -> {
            if(e instanceof PlayerMoveEvent moveEvent) {
                var player = moveEvent.getPlayer();
                if(playerEnterTown.containsKey(player)) {
                    var townData = playerEnterTown.get(player);
                    var loc = moveEvent.getTo();
                    var currentTownData = Selector.getTownFromLocation(loc);
                    if(currentTownData == null || townData != currentTownData) {
                        //町から出ました
                        var newData = new PlayerExitTown();
                        newData.player = player;
                        newData.previousTownData = townData;

                        playerEnterTown.remove(player);

                        run(newData);
                    }
                }
            }
        });
    }

    @EventHandler
    public void OnPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        run(event);
    }

    @EventHandler
    public void OnPlayerMoving(PlayerMoveEvent event) {
        run(event);
    }
}
