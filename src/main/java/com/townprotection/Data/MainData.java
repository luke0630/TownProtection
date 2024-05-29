package com.townprotection.Data;

import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.SelectorData.SelectorData;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainData {
    public static Map<Player, SelectorData> playerSelectData = new HashMap<>();
    public static Map<Integer, SelectorMarkData> markData = new HashMap<>();
}
