package com.townprotection.Data.Range;

import org.bukkit.Location;
import org.bukkit.Material;

public class ShowRangeData {
    private Material material;
    private Location location;

    public ShowRangeData(Material material, Location location) {
        this.material = material;
        this.location = location;
    }

    public Material getMaterial() {
        return material;
    }

    public Location getLocation() {
        return location;
    }
}
