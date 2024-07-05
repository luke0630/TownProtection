package com.townprotection.CommandRun;

import com.townprotection.Data.MainData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainCommandTabComplete implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("townprotection")) return null;
        if (args.length == 1) {
            List<String> completions = Arrays.asList("wand", "open", "show", "deselect");
            return completions.stream()
                    .filter(option -> option.startsWith(args[0]))
                    .collect(Collectors.toList());
        } else if(args.length == 2) {
            switch(args[0]) {
                case "open" -> {
                    List<String> towns = new ArrayList<>();
                    for(var townData : MainData.townMarkData) {
                        towns.add(townData.townName);
                    }
                    return towns;
                }
            }
        }
        return null;
    }
}
