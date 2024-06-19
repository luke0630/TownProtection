package com.townprotection.Data.MarkData;

import com.townprotection.Data.SelectorData.SelectorData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

public class SelectorMarkData implements ConfigurationSerializable {
    public SelectorData selectorData = new SelectorData();
    public String displayName = "名無し土地";
    public UUID owner; //土地のすべてを作った人
    public List<UUID> manager = new ArrayList<>(); //土地管理者
    public List<UUID> allowedPlayer = new ArrayList<>();
    public List<ActionList.Action> allowActionList = new ArrayList<>();

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(this));
            }
        } catch (IllegalAccessException ignored) {
        }
        return map;
    }

}
