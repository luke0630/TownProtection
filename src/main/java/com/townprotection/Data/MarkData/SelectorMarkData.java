package com.townprotection.Data.MarkData;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.SelectorData.SelectorData;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SelectorMarkData extends DataAbstract {
    public SelectorData selectorData = new SelectorData();
    public List<UUID> manager = new ArrayList<>(); //土地管理者
    public List<UUID> allowedPlayer = new ArrayList<>();
    public List<ActionList.Action> allowActionList = new ArrayList<>();

    public SelectorMarkData(Material icon, String name, CreationDate date) {
        super(icon, name, date);
    }
}
