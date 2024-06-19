package com.townprotection.Effect.EffectList.System;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class EffectInformation {
    private String title = "";
    private Material icon = Material.SCULK_SENSOR;
    private List<String> description = new ArrayList<>();

    // コンストラクタ、getter、setter
    public EffectInformation(String title, List<String> description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }
    public Material getIcon() {
        return this.icon;
    }


    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }
}
