package com.townprotection.Data.ActionData;

import org.bukkit.Material;

public class ActionData {
    public String name;
    public Material displayMaterial;
    public ActionData(String name,Material material) {
        this.name = name;
        this.displayMaterial = material;
    }
}
