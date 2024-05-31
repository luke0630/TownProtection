package com.townprotection.System;

import org.bukkit.Location;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class CacheManager {
    private static WeakHashMap<String, WeakReference<Location>> locationCache = new WeakHashMap<>();

    public static void cacheLocation(String key, Location location) {
        locationCache.put(key, new WeakReference<>(location));
    }

    public static Location getCachedLocation(String key) {
        WeakReference<Location> ref = locationCache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }
}