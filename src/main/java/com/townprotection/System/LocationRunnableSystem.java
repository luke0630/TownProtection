package com.townprotection.System;


import org.bukkit.Location;

public class LocationRunnableSystem {

    @FunctionalInterface
    public interface LocationRunnable {
        void run(Location location);
    }
}
