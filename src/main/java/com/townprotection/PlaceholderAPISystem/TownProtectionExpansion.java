package com.townprotection.PlaceholderAPISystem;

import com.townprotection.Selector.Selector;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TownProtectionExpansion extends PlaceholderExpansion {

    @Override
    @NotNull
    public String getAuthor() {
        return "Luke0630";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "townprotection";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }


    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if(!(offlinePlayer instanceof Player player)) {
            return "";
        }
        if (params.equals("currenttown")) {
            var town = Selector.getTownFromLocation(player.getLocation());
            if(town == null) {
                return "---";
            }
            return town.townName;
        }
        if(params.equals("currentmarked")) {
            var town = Selector.getTownFromLocation(player.getLocation());
            if(town == null) {
                return "---";
            }
            var marked = Selector.getMarkDataFromLocation(town, player.getLocation());
            if(marked == null) {
                return "---";
            }
            return marked.displayName;
        }

        return null; //
    }
}

