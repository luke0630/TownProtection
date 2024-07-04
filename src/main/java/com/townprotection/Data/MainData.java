package com.townprotection.Data;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.System.RunnableSystem;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainData {
    public static Map<Player, SelectorData> playerSelectData = new HashMap<>();

    public static List<TownData> townMarkData = new ArrayList<>(); //町のデータ

    public static Map<Player, GUIData> playerOpenGUI = new HashMap<>();

    public static Map<Player, RunnableSystem.Runnable> setNameRunnable = new HashMap<>();
    public static Map<Player, Integer> listPage = new HashMap<>();
}
