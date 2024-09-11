package com.townprotection.Listener.OriginalListener;

import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import org.bukkit.entity.Player;

public class PlayerEnterTown {
    public Player player;
    public TownData townData = new TownData(null, null, null);
    public SelectorMarkData markData = new SelectorMarkData(null, null, null);
}
