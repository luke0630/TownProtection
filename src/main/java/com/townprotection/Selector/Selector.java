package com.townprotection.Selector;

import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.Listener.CallBackListener;
import com.townprotection.Range.ShowRange;
import com.townprotection.System.RunnableSystem;
import com.townprotection.TownProtection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.townprotection.Data.MainData.playerSelectData;
import static com.townprotection.Data.MainData.townMarkData;
import static com.townprotection.Range.ShowRange.RemoveParticle;
import static com.townprotection.Range.ShowRange.RemoveShowRange;
import static com.townprotection.Range.ShowRangeWhenEnter.ShowTownAndMarked;
import static com.townprotection.TownProtection.Save;
import static com.townprotection.Useful.*;


public class Selector {

    public static void SetRangeWithToolInitialization() {
        CallBackListener.AddCallBack((Object e) -> {
            if(e instanceof PlayerInteractEvent event) {
                var player = event.getPlayer();
                if(event.getClickedBlock() == null) return;
                if(!Selector.IsSelectorTool(player)) return;

                if(!playerSelectData.containsKey(player)) {
                    playerSelectData.put(player, new SelectorData());
                }

                var data = playerSelectData.get(player);
                var loc = event.getClickedBlock().getLocation();

                String locMessage = "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ();

                if(event.getAction().isLeftClick()) {
                    data.startBlock = loc;
                    player.sendMessage(toColor("&d開始地点を指定しました。 " + locMessage));
                } else if(event.getAction().isRightClick()) {
                    if(event.getHand() == EquipmentSlot.HAND) return;
                    data.endBlock = loc;
                    player.sendMessage(toColor("&d終了地点を指定しました。 " + locMessage));
                }
                event.setCancelled(true);

                if(data.startBlock != null && data.endBlock != null) {
                    getRange(player, data, Color.AQUA);
                    if(showModePlayer.contains(player)) return;
                    RemoveShowRange(player);
                    for(var town : townMarkData) {
                        if(overlaps(town.rangeOfTown, playerSelectData.get(player))) {
                            ShowTownAndMarked(player, town, false);
                        }
                    }
                }
            }
        });
    }
    public static boolean IsSelectorToolWithItem(ItemStack itemStack) {
        if(itemStack.getType() == Material.WOODEN_PICKAXE) {
            if(itemStack.getLore() == null) return false;
            if (itemStack.getLore().get(0).equalsIgnoreCase(GiveSelector.SELECTOR_LORE)) return true;
        }
        return false;
    }

    public static boolean IsSelectorTool(Player player) {
        if(IsSelectorToolWithItem(player.getInventory().getItemInMainHand())) {
            return true;
        }
        return false;
    }
    public static Map<Player, BukkitTask> actionBarSchedulers = new HashMap<>();
    public static Map<Player, List<BukkitTask>> schedulers = new HashMap<>();
    public static boolean getRange(Player player, SelectorData data, Color color) {
        if (Selector.schedulers.containsKey(player)) {
            for (var schedulers : Selector.schedulers.get(player)) {
                schedulers.cancel();
            }
            Selector.schedulers.get(player).clear();
        }
        var start = data.startBlock;
        var end = data.endBlock;

        int distanceZ = Math.abs(start.getBlockZ() - end.getBlockZ()) + 1;
        int distanceX = Math.abs(start.getBlockX() - end.getBlockX()) + 1;
        int totalBlock = distanceX * distanceZ;
        if (totalBlock >= 1000 && player != null) {
            player.sendActionBar(toColor("&c&l1000ブロック以上なため、パーティクルの表示はできません。(高負荷を防ぐため)"));
            return false;
        } else {
            showRange(player, data, (Object object) -> {
                if (object instanceof Location location) {
                    DrawParticleBlock(location, player, color);
                }
            });
        }
        return true;
    }

    public static List<Location> showRange(Player player, SelectorData data, RunnableSystem.Runnable runnable) {
        var start = data.startBlock;
        var end = data.endBlock;

        var xDifference = start.getBlockX() - end.getBlockX();
        xDifference += (int) Math.signum(xDifference);

        var zDifference = start.getBlockZ() - end.getBlockZ();
        zDifference += (int) Math.signum(zDifference);

        var current = new Location(start.getWorld(),0,0,0);
        current = start.clone();


        if(Math.abs(xDifference) == 0) {
            xDifference+=1;
        }
        if(Math.abs(zDifference) == 0) {
            zDifference+=1;
        }

        List<Location> resultBlocks = new ArrayList<>();
        for(int a=0;a < Math.abs(zDifference);a++) {
            for (int i = 0; i < Math.abs(xDifference); i++) {
                current.setX(current.getBlockX());
                if (player != null) {
                    if(isEdgeBlock(current, start,end)) {
                        resultBlocks.add(current);
                        if(runnable != null) {
                            runnable.run(current);
                        }
                    }
                }

                current.setX(current.getBlockX() + (-1) * Math.signum(xDifference));
            }

            current.setX(start.getBlockX());
            current.setZ(current.getBlockZ() + (-1) * Math.signum(zDifference));
        }
        return resultBlocks;
    }

    // エッジのブロックを判定するヘルパーメソッド
    private static boolean isEdgeBlock(Location location, Location start, Location end) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int xStart = Math.min(start.getBlockX(), end.getBlockX());
        int xEnd = Math.max(start.getBlockX(), end.getBlockX());
        int zStart = Math.min(start.getBlockZ(), end.getBlockZ());
        int zEnd = Math.max(start.getBlockZ(), end.getBlockZ());

        return (x == xStart || x == xEnd) || (z == zStart || z == zEnd);
    }

    static void DrawParticleBlock(Location start, Player player,Color color) {
        if(color == null) {
            return;
        }
        Vector offset = start.clone().toVector();
        start = ShowRange.getHighBlock(start, 1).get(0);

        Vector basisX = new Vector(1, 0, 0);
        Vector basisY = new Vector(0, 1, 0);
        Vector basisZ = new Vector(0, 0, 1);

        Location finalStart = start;
        var task = Bukkit.getScheduler().runTaskTimer(TownProtection.instance, () -> {
            targetPlayer = player;
            drawParticle(basisY, offset.clone().toLocation(finalStart.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisX).toLocation(finalStart.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisX).add(basisZ).toLocation(finalStart.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisZ).toLocation(finalStart.getWorld()), color);

            drawParticle(basisX, offset.clone().toLocation(finalStart.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisY).toLocation(finalStart.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisZ).toLocation(finalStart.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisY).add(basisZ).toLocation(finalStart.getWorld()), color);

            drawParticle(basisZ, offset.clone().toLocation(finalStart.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisX).toLocation(finalStart.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisY).toLocation(finalStart.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisY).add(basisX).toLocation(finalStart.getWorld()), color);
        }, 0L, 15L);

        if (schedulers.containsKey(player)) {
            schedulers.get(player).add(task);
        } else {
            List<BukkitTask> tasks = new ArrayList<>();
            tasks.add(task);
            schedulers.put(player, tasks);
        }
    }
    static final double SPACE = 0.5f;
    static Player targetPlayer;
    private static void drawParticle(Vector toDraw, Location offset, Color color) {
        for(double d=0; d<=toDraw.length(); d+= Selector.SPACE) {
            Location toSpawn = offset.clone().add(toDraw.clone().multiply(d));
            targetPlayer.spawnParticle(Particle.REDSTONE, toSpawn, 2, 0, 0, 0, 0, new Particle.DustOptions(color, 0.7f)); //特定のプレイヤーのみ実行させる
        }
    }


    // 選択範囲が他の範囲と重なっているか確認するメソッド
    public static boolean overlaps(SelectorData parent, SelectorData child) {
        if (parent == null || parent.startBlock == null) {
            // エラーメッセージをログに記録するか、例外をスローする
            return false;
        }
        int thisStartX = Math.min(parent.startBlock.getBlockX(), parent.endBlock.getBlockX());
        int thisEndX = Math.max(parent.startBlock.getBlockX(), parent.endBlock.getBlockX());
        int thisStartZ = Math.min(parent.startBlock.getBlockZ(), parent.endBlock.getBlockZ());
        int thisEndZ = Math.max(parent.startBlock.getBlockZ(), parent.endBlock.getBlockZ());

        int otherStartX = Math.min(child.startBlock.getBlockX(), child.endBlock.getBlockX());
        int otherEndX = Math.max(child.startBlock.getBlockX(), child.endBlock.getBlockX());
        int otherStartZ = Math.min(child.startBlock.getBlockZ(), child.endBlock.getBlockZ());
        int otherEndZ = Math.max(child.startBlock.getBlockZ(), child.endBlock.getBlockZ());

        // X方向での重なりをチェック
        if (thisEndX < otherStartX || thisStartX > otherEndX) {
            return false;
        }

        // Z方向での重なりをチェック
        if (thisEndZ < otherStartZ || thisStartZ > otherEndZ) {
            return false;
        }

        // X方向とZ方向の両方で重なっている場合
        return true;
    }

    //childが、parentの範囲内かどうか
    public static boolean isRangeInRange(SelectorData parent, SelectorData child) {
        var startX = parent.startBlock.getBlockX();
        var endX = parent.endBlock.getBlockX();
        var startZ = parent.startBlock.getBlockZ();
        var endZ = parent.endBlock.getBlockZ();

        // 町の範囲の最小値と最大値を求める
        int minX = Math.min(startX, endX);
        int maxX = Math.max(startX, endX);
        int minZ = Math.min(startZ, endZ);
        int maxZ = Math.max(startZ, endZ);

        // 選択範囲の開始位置と終了位置
        var start = child.startBlock;
        var end = child.endBlock;

        int selectStartX = start.getBlockX();
        int selectEndX = end.getBlockX();
        int selectStartZ = start.getBlockZ();
        int selectEndZ = end.getBlockZ();


        // 選択範囲の最小値と最大値を求める
        int selectMinX = Math.min(selectStartX, selectEndX);
        int selectMaxX = Math.max(selectStartX, selectEndX);
        int selectMinZ = Math.min(selectStartZ, selectEndZ);
        int selectMaxZ = Math.max(selectStartZ, selectEndZ);

        // townStartX と townEndX の間に選択範囲全体 (selectMinX と selectMaxX) があるかどうかを確認
        if ((selectMinX >= minX && selectMaxX <= maxX) && (selectMinZ >= minZ && selectMaxZ <= maxZ)) {
            return true;
        }
        return false;
    }


    public static TownData getTownFromLocation(Location loc) {
        for(var town : townMarkData) {
            var pos = new SelectorData();
            pos.startBlock = loc;
            pos.endBlock = loc;
            if(overlaps(town.rangeOfTown, pos)) {
                return town;
            }
        }
        return null;
    }
    public static SelectorMarkData getMarkDataFromLocation(TownData town, Location loc) {
        for(var data : town.selectorMarkData) {
            var pos = new SelectorData();
            pos.startBlock = loc;
            pos.endBlock = loc;
            if(overlaps(data.selectorData, pos)) {
                return data;
            }
        }
        return null;
    }


    public static void ApplyChangeSelector(Player player) {
        player.sendMessage(toColor("&c--------------------------------------"));
        player.closeInventory();
        if(!Selector.CanApply(player)) {
            player.sendMessage(toColor("&a適用ができませんでした。上のメッセージに原因があります。もう一度試してください。"));
        } else {
            var previousData = changeSelectorDataPlayer.get(player);
            if(previousData.getMarkData() == null) {
                var newTownData = previousData.getTownData();
                newTownData.rangeOfTown = playerSelectData.get(player).clone();
                previousData.setTownData(newTownData);
                Save();
            } else if(previousData.getMarkData() != null) { //土地の編集だった場合
                var newMarkedData = previousData.getMarkData();
                newMarkedData.selectorData = playerSelectData.get(player).clone();
                previousData.setMarkData(newMarkedData);
                Save();
            }
            player.sendMessage("適用しました。");

            RemoveParticle(player);
            playerSelectData.remove(player);

            RemoveShowRange(player);
            ShowTownAndMarked(player, changeSelectorDataPlayer.get(player).getTownData(), false);
            changeSelectorDataPlayer.remove(player);
        }
        player.sendMessage(toColor("&c--------------------------------------"));
    }
    public static class TownAndMarked {
        private TownData townData;

        public TownAndMarked(TownData townData, SelectorMarkData markData) {
            this.townData = townData;
            this.markData = markData;
        }

        private SelectorMarkData markData;

        public TownData getTownData() {
            return townData;
        }

        public void setTownData(TownData townData) {
            this.townData = townData;
        }

        public SelectorMarkData getMarkData() {
            return markData;
        }

        public void setMarkData(SelectorMarkData markData) {
            this.markData = markData;
        }
    }

    public static Map<Player, TownAndMarked> changeSelectorDataPlayer = new HashMap<>();

    public static void ChangeTownSelectorData(Player player, TownData townData) {
        var currentTownData = Selector.getTownFromLocation(player.getLocation());
        if(currentTownData != null && currentTownData.equals(townData)) {
            ChangeSelectorData(player, townData ,null, townData.townName);
        } else {
            player.sendMessage(toColor("&c町の範囲を変更する場合はあなた自身がその変更したい町に現在いる必要があります。"));
        }
    }
    public static void ChangeMarkedDataSelector(Player player, SelectorMarkData markData) {
        var currentTownData = Selector.getTownFromLocation(player.getLocation());
        if(currentTownData == null) {
            player.sendMessage(toColor("&c土地の範囲を変更する場合はあなた自身がその変更したい土地に現在いる必要があります。"));
            return;
        }
        var currentMarkData = Selector.getMarkDataFromLocation(currentTownData, player.getLocation());
        if(currentMarkData != null && currentMarkData.equals(markData)) {
            ChangeSelectorData(player, currentTownData ,currentMarkData, currentTownData.townName);
        } else {
            player.sendMessage(toColor("&c土地の範囲を変更する場合はあなた自身がその変更したい土地に現在いる必要があります。"));
        }
    }

    public static void ChangeSelectorData(Player player, TownData townData, SelectorMarkData selectorMarkData, String name) {
        player.closeInventory();

        ShowActionBar(player, "&cブロック右クリックで開始地点、左クリックで終了地点、/twで適用します。");
        player.sendMessage(toColor("&c--------------------------------------------------"));
        player.sendMessage(toColor(name + " &f&lの範囲の変更を行います。"));
        player.sendMessage(toColor("&aブロックを左クリックで開始地点、右クリックで終了地点を変更します。"));
        player.sendMessage(toColor("&c&l現在範囲を表示しています。&a&l/twコマンドで適用します。"));
        player.sendMessage(toColor("&c--------------------------------------------------"));

        SelectorData chooseData;
        ShowRange.ShowRangeWithBlock(player, townData.rangeOfTown, Material.LIME_WOOL, true);
        if(selectorMarkData == null) {
            chooseData = townData.rangeOfTown.clone();
        } else {
            chooseData = selectorMarkData.selectorData.clone();
            for(var showSelectorData : townData.selectorMarkData) {
                ShowRange.ShowRangeWithBlock(player, showSelectorData.selectorData, Material.RED_WOOL, false);
            }
        }
        playerSelectData.put(player, chooseData);
        getRange(player, chooseData, Color.ORANGE);

        for(var showSelectorData : townData.selectorMarkData) {
            ShowRange.ShowRangeWithBlock(player, showSelectorData.selectorData, Material.RED_WOOL, false);
        }

        ShowRange.ShowRangeWithBlock(player, chooseData, Material.GOLD_BLOCK, false);

        changeSelectorDataPlayer.put(player, new TownAndMarked(townData, selectorMarkData));
        CallBackListener.AddCallBack((Object e) -> {
            if(e instanceof PlayerInteractEvent event) {
                if(!changeSelectorDataPlayer.containsKey(player)) return;
                var changeData = changeSelectorDataPlayer.get(player);
                if(event.getAction().isRightClick()) {
                    if(event.getHand() == EquipmentSlot.HAND) {
                        return;
                    }
                }
                if(event.getClickedBlock() == null) return;
                if(!Selector.IsSelectorTool(player)) return;

                getRange(player, chooseData, Color.ORANGE);
                ShowRange.RemoveShowRange(player);

                if(changeData.markData != null) {
                    ShowRange.ShowRangeWithBlock(player, changeData.getTownData().rangeOfTown, Material.LIME_WOOL, false);
                } else {
                    //町の範囲の編集
                    for(var data : townMarkData) {
                        if(data.equals(changeData.getTownData())) continue;
                        if(overlaps(data.rangeOfTown, playerSelectData.get(player))) {
                            ShowRange.ShowRangeWithBlock(player, data.rangeOfTown, Material.REDSTONE_LAMP, false);
                        }
                    }
                }

                for(var showSelectorData : townData.selectorMarkData) {
                    if(changeData.markData != null && showSelectorData.equals(changeData.getMarkData())) continue;
                    ShowRange.ShowRangeWithBlock(player, showSelectorData.selectorData, Material.RED_WOOL, false);
                }
                ShowRange.ShowRangeWithBlock(player, chooseData, Material.GOLD_BLOCK, false);
                CanApply(player);
            }
        });
    }

    public static boolean CanApply(Player player) {
        boolean isTrue = true;
        var changeData = changeSelectorDataPlayer.get(player);
        if(changeData.markData != null) {
            for(var markedData : changeData.getTownData().selectorMarkData) {
                if(changeData.markData.selectorData.equals(markedData.selectorData)) continue;
                if(overlaps(playerSelectData.get(player), markedData.selectorData)) {
                    player.sendMessage(toColor("&cほかの土地に重なっています！"));
                    isTrue = false;
                }
            }
            if(!Selector.isRangeInRange(changeData.getTownData().rangeOfTown, playerSelectData.get(player))) {
                player.sendMessage(toColor("&c緑色の範囲(町)に収める必要があります！"));
                isTrue = false;
            }
        } else {
            for(var townData : townMarkData) {
                if(townData.equals(changeData.getTownData())) continue;
                if(overlaps(townData.rangeOfTown, playerSelectData.get(player))) {
                    player.sendMessage(toColor("&cほかの町の範囲に重なっています！"));
                    isTrue = false;
                    break;
                }
            }
            for(var marked : changeData.getTownData().selectorMarkData) {
                if(!isRangeInRange(playerSelectData.get(player), marked.selectorData)) {
                    player.sendMessage(toColor("&c町の範囲から町の土地が飛び出してしまっています！町の土地が収まるようにサイズを変更してください。"));
                    isTrue = false;
                    break;
                }
            }
        }
        return isTrue;
    }

    public static List<Player> showModePlayer = new ArrayList<>();
    public static void ShowMode(Player player) {
        RemoveShowRange(player);
        if(showModePlayer.contains(player)) {
            showModePlayer.remove(player);
            var data = Selector.getTownFromLocation(player.getLocation());
            if(data != null) {
                RemoveShowRange(player);
                ShowTownAndMarked(player, data, true);
            }
            player.sendMessage(toColor("&a&lオール表示モードから抜けました。"));
        } else {
            for(var town : townMarkData) {
                ShowTownAndMarked(player, town, false);
            }
            showModePlayer.add(player);
            HiddenActionBar(player);

            player.sendMessage(toColor("&a&lオール表示モードに入りました。すべての町と土地が表示されます。"));
        }
    }
}

