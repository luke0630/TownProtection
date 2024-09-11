package com.townprotection;

import com.townprotection.Data.DataAbstract;
import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MainData;
import com.townprotection.GUI.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.luke.takoyakiLibrary.TakoUtility;

import java.time.LocalDate;
import java.util.*;

import static com.townprotection.Data.MainData.playerOpenGUI;
import static com.townprotection.Selector.Selector.actionBarSchedulers;
import static com.townprotection.TownProtection.getManager;

public class Useful {
    public static String toColor(String message) {
        if(message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Inventory getInv(Integer size, String title) {
        return Bukkit.createInventory(null,size,Useful.toColor(title));
    }

    public static ItemStack getItem(Material material, String name) {
        var item = new ItemStack(material);
        var meta = item.getItemMeta();

        meta.setDisplayName(toColor(name));

        item.setItemMeta(meta);
        return item;
    }

    public static List<String> uuidToString(List<UUID> uuidList) {
        var list = new ArrayList<String>();
        for(var string : uuidList) {
            list.add(string.toString());
        }
        return list;
    }

    public static List<UUID> stringToUUID(List<String> stringList) {
        var list = new ArrayList<UUID>();
        for(var string : stringList) {
            list.add(UUID.fromString(string));
        }
        return list;
    }

    public static void setLore(ItemStack item, List<String> lore) {
        var meta = item.getItemMeta();
        var resultLore = new ArrayList<String>();
        for(var l : lore) {
            resultLore.add(toColor(l));
        }
        meta.setLore(resultLore);
        item.setItemMeta(meta);
    }

    public static String getXYZMessage(Location location) {
        return toColor("&c&lX: "+location.getBlockX() + " Y: " + location.getBlockY() + " Z: " + location.getBlockZ());
    }

    public static String getBooleanInJapanese(Boolean bool) {
        if(bool) {
            return "&b&l有効";
        } else {
            return "&c&l無効";
        }
    }

    public static void ShowActionBar(Player player, String message) {
        HiddenActionBar(player);
        actionBarSchedulers.put(player, Bukkit.getScheduler().runTaskTimerAsynchronously(TownProtection.instance, () -> {
            player.sendActionBar(toColor(message));
        }, 0, 20L));
    }

    public static void HiddenActionBar(Player player) {
        if(actionBarSchedulers.containsKey(player)) {
            actionBarSchedulers.get(player).cancel();
            actionBarSchedulers.remove(player);
        }
    }

    public static void setBottomControlOnGUI(Inventory inventory) {
        var size = inventory.getSize();
        int lines = size / 9;
        lines--; //index対応
        for(int i=lines*9;i < size;i++) {
            inventory.setItem(i,Useful.getItem(Material.BLACK_STAINED_GLASS_PANE," "));
        }
    }

    public static ItemStack getPlayerHead(UUID uuid) {
        ItemStack skull = getItem(Material.PLAYER_HEAD,"&f&l"+ Bukkit.getOfflinePlayer(uuid).getName());
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        skull.setItemMeta(skullMeta);
        return skull;
    }


    public static Location getPistonNextLocation(Location previousLoc, BlockFace face) {
        var changeData = previousLoc.clone();
        if(face == BlockFace.NORTH) {
            changeData.setZ(previousLoc.getBlockZ()-1);
        } else if(face == BlockFace.EAST) {
            changeData.setX(previousLoc.getBlockX()-1);
        } else if(face == BlockFace.SOUTH) {
            changeData.setZ(previousLoc.getBlockZ()+1);
        } else if(face == BlockFace.WEST) {
            changeData.setX(previousLoc.getBlockX()+1);
        }

        return changeData;
    }

    public static DataAbstract.CreationDate getCurrentDate() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        return new DataAbstract.CreationDate(year,month,day);
    }


    // ======= Filter Controller Systems =======
    public static void clickFilterFunction(InventoryClickEvent event, GuiManager.ListGUIPreset gui) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();
        if(slot == 9*5+6) {
            MainData.Filter currentFilter = playerOpenGUI.get(player).currentFilter;
            int currentFilterIndex = Arrays.asList(MainData.Filter.values()).indexOf(currentFilter);
            int nextFilter = currentFilterIndex+1;
            if(nextFilter >= MainData.Filter.values().length) {
                nextFilter = 0;
            }
            playerOpenGUI.get(player).currentFilter = MainData.Filter.values()[nextFilter];
            getManager().OpenListGUI(player, gui);
        }
    }

    public static ItemStack getFilterItem(Player player) {
        if(!playerOpenGUI.containsKey(player)) {
            playerOpenGUI.put(player, new GUIData());
        }
        var filterItem = TakoUtility.getItem(Material.COMPARATOR, "&aフィルター");
        List<String> filters = new ArrayList<>();
        for(var filter : MainData.Filter.values()) {
            String resultFilter = filter.getString();
            if(playerOpenGUI.get(player).currentFilter == filter) {
                resultFilter = toColor("&f・" + resultFilter + " ←");
            }
            filters.add(toColor("&8" + resultFilter));
        }
        filters.addAll(List.of(
                "&f-----------------",
                "&6クリックしてフィルターを変更する"
        ));
        TakoUtility.setLore(filterItem, filters);
        return filterItem;
    }

    public static List<DataAbstract> getDataByFilter(List<DataAbstract> dataAbstractList, Player player){
        if(playerOpenGUI.isEmpty()) {
            playerOpenGUI.put(player, new GUIData());
        }

        MainData.Filter currentFilter = playerOpenGUI.get(player).currentFilter;
        switch(currentFilter) {
            //最新順
            case LATEST -> {
                return getDateByLatest(dataAbstractList);
            }
            case OLDEST -> {
                var list = getDateByLatest(dataAbstractList);
                Collections.reverse(list);
                return list;
            }
            case YOU_OWNER -> {
                var result = new ArrayList<DataAbstract>();
                for(DataAbstract data : dataAbstractList) {
                    if(data.getOwner().toString().equals(player.getUniqueId().toString())) {
                        result.add(data);
                    }
                }
                return result;
            }
        }
        return dataAbstractList;
    }

    private static List<DataAbstract> getDateByLatest(List<DataAbstract> dataAbstractList) {
        var addingList = new ArrayList<DataAbstract>();
        for(DataAbstract townData : dataAbstractList) {
            if(addingList.isEmpty()) {
                addingList.add(townData);
            } else {
                //addingListをまわして、getDifferenceByDayを使用して大きいか小さいかを使用してindexを決めていく。すべて回し終わった後にindexが決定し、その場所に対象の町データが追加される。
                int resultIndex = 0;
                int forIndex = 0;
                for(var adding : addingList) {
                    var signum = Math.signum(adding.getCreationDate().getDifferenceByDay(townData.getCreationDate()));
                    if(signum == -1.0) {
                        //現在のaddingより早く作成された場合
                        if(resultIndex > 0) {
                            resultIndex--;
                        }
                    } else if(signum == 1.0) {
                        //現在のaddingより遅く作成された場合
                        resultIndex++;
                    } else if(signum == 0) {
                        resultIndex = forIndex;
                    }
                    forIndex++;
                }
                addingList.add(resultIndex, townData);
            }
        }
        return addingList;
    }
}
