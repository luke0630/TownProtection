package com.townprotection.Selector;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.townprotection.Data.MarkData.SelectorMarkData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.System.RunnableSystem;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.townprotection.Data.MainData.townMarkData;


public class Selector {
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
    public static Map<Player, List<BukkitTask>> schedulers = new HashMap<>();
    public static boolean getRange(Player player, SelectorData data, Color color) {
        var start = data.startBlock;
        var end = data.endBlock;

        int distanceZ = Math.abs(start.getBlockZ() - end.getBlockZ()) + 1;
        int distanceX = Math.abs(start.getBlockX() - end.getBlockX()) + 1;
        int totalBlock = distanceX * distanceZ;
        if (totalBlock >= 1000 && player != null) {
            player.sendActionBar(Useful.toColor("&c&l1000ブロック以上なため、パーティクルの表示はできません。(高負荷を防ぐため)"));
            return false;
        } else {
            showRange(player, data, (Object object) -> {
                if(object instanceof Location location) {
                    DrawParticleBlock(location, player, color);
                }
            });
        }
        return true;
    }

    public static void showRange(Player player, SelectorData data, RunnableSystem.Runnable runnable) {
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
        for(int a=0;a < Math.abs(zDifference);a++) {
            for (int i = 0; i < Math.abs(xDifference); i++) {
                current.setX(current.getBlockX());
                if (player != null) {
                    if(isEdgeBlock(current, start,end)) {
                        runnable.run(current);
                    }
                }

                current.setX(current.getBlockX() + (-1) * Math.signum(xDifference));
            }

            current.setX(start.getBlockX());
            current.setZ(current.getBlockZ() + (-1) * Math.signum(zDifference));
        }
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

        Vector basisX = new Vector(1, 0, 0);
        Vector basisY = new Vector(0, 1, 0);
        Vector basisZ = new Vector(0, 0, 1);

        var task = Bukkit.getScheduler().runTaskTimer(TownProtection.instance, () -> {
            targetPlayer = player;
            drawParticle(basisY, offset.clone().toLocation(start.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisX).toLocation(start.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisX).add(basisZ).toLocation(start.getWorld()), color);
            drawParticle(basisY, offset.clone().add(basisZ).toLocation(start.getWorld()), color);

            drawParticle(basisX, offset.clone().toLocation(start.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisY).toLocation(start.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisZ).toLocation(start.getWorld()), color);
            drawParticle(basisX, offset.clone().add(basisY).add(basisZ).toLocation(start.getWorld()), color);

            drawParticle(basisZ, offset.clone().toLocation(start.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisX).toLocation(start.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisY).toLocation(start.getWorld()), color);
            drawParticle(basisZ, offset.clone().add(basisY).add(basisX).toLocation(start.getWorld()), color);
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
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        for(double d=0; d<=toDraw.length(); d+= Selector.SPACE) {
            Location toSpawn = offset.clone().add(toDraw.clone().multiply(d));
            //toSpawn.getWorld().spawnParticle(Particle.REDSTONE, toSpawn, 2, 0, 0, 0, 0, new Particle.DustOptions(color  , 0.7f),true);
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
}
