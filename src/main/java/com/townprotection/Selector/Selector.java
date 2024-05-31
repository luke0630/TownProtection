package com.townprotection.Selector;

import com.townprotection.Data.MainData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.System.LocationRunnableSystem;
import com.townprotection.TownProtection;
import com.townprotection.Useful;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.lang.ref.WeakReference;
import java.util.*;


public class Selector {
    public static Map<Player, List<BukkitTask>> schedulers = new HashMap<>();
    public static void getRange(Player player, SelectorData data, Color color) {
        var start = data.startBlock;
        var end = data.endBlock;

        int distanceZ = Math.abs(start.getBlockZ() - end.getBlockZ()) + 1;
        int distanceX = Math.abs(start.getBlockX() - end.getBlockX()) + 1;
        int totalBlock = distanceX * distanceZ;
        if (totalBlock >= 500 && player != null) {
            player.sendActionBar(Useful.toColor("&c&l300ブロック以上なため、パーティクルの表示はできません。(高負荷を防ぐため)"));
        }

        var xDifference = start.getBlockX() - end.getBlockX();
        xDifference += (int) Math.signum(xDifference);

        var zDifference = start.getBlockZ() - end.getBlockZ();
        zDifference += (int) Math.signum(zDifference);

        var current = new Location(start.getWorld(), 0, 0, 0);
        current = start.clone();

        for (int a = 0; a < Math.abs(zDifference); a++) {
            current.setX(current.getBlockX());

            for (int i = 0; i < Math.abs(xDifference); i++) {
                if (player != null) {
                    boolean b = i == Math.abs(xDifference) - 1 && a == Math.abs(zDifference) - 1;
                    if (i == 0 && a == 0) {
                        DrawParticleBlock(current, player, color, true);
                    } else if (i == Math.abs(xDifference) - 1 && a == 0) {
                        DrawParticleBlock(current, player, color, true);
                    }
                    if (i != 0 && a == 0) {
                        DrawParticleBlock(current, player, color, false);
                    }
                    if (i == Math.abs(xDifference) - 1) {
                        DrawParticleBlock(current, player, color, false);
                    }
                    if (i == 0) {
                        DrawParticleBlock(current, player, color, false);
                    }
                    if (a == Math.abs(zDifference) - 1) {
                        DrawParticleBlock(current, player, color, false);
                    }

                    if (i == 0 && a == Math.abs(zDifference) - 1) {
                        DrawParticleBlock(current, player, color, true);
                    } else if (b) {
                        DrawParticleBlock(current, player, color, true);
                    }
                }

                current.setX(current.getBlockX() + (-1) * Math.signum(xDifference));
            }

            current.setX(start.getBlockX());
            current.setZ(current.getBlockZ() + (-1) * Math.signum(zDifference));
        }
    }

    // エッジのブロックを判定するヘルパーメソッド
    private static boolean isEdgeBlock(Location location, int xDifference, int zDifference, Location start, Location end) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int xStart = Math.min(start.getBlockX(), end.getBlockX());
        int xEnd = Math.max(start.getBlockX(), end.getBlockX());
        int zStart = Math.min(start.getBlockZ(), end.getBlockZ());
        int zEnd = Math.max(start.getBlockZ(), end.getBlockZ());

        return (x == xStart || x == xEnd) || (z == zStart || z == zEnd);
    }

    static void DrawParticleBlock(Location start, Player player,Color color, boolean includeHigh) {
        if(color == null) {
            return;
        }
        Vector offset = start.clone().toVector();

        Vector basisX = new Vector(1, 0, 0);
        Vector basisY = new Vector(0, 1, 0);
        Vector basisZ = new Vector(0, 0, 1);

        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(TownProtection.instance, () -> {
            drawParticle(basisY, offset.clone().toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisY, offset.clone().add(basisX).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisY, offset.clone().add(basisX).add(basisZ).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisY, offset.clone().add(basisZ).toLocation(start.getWorld()), SPACE, color);

            drawParticle(basisX, offset.clone().toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisX, offset.clone().add(basisY).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisX, offset.clone().add(basisZ).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisX, offset.clone().add(basisY).add(basisZ).toLocation(start.getWorld()), SPACE, color);

            drawParticle(basisZ, offset.clone().toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisZ, offset.clone().add(basisX).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisZ, offset.clone().add(basisY).toLocation(start.getWorld()), SPACE, color);
            drawParticle(basisZ, offset.clone().add(basisY).add(basisX).toLocation(start.getWorld()), SPACE, color);

            // Optional: Draw particles above the block
            if(includeHigh) {
                /*drawParticle(new Vector(0, 4, 0), offset.clone().toLocation(start.getWorld()), 0.15, color);
                drawParticle(new Vector(0, 4, 0), offset.clone().add(basisZ).toLocation(start.getWorld()), 0.15, color);
                drawParticle(new Vector(0, 4, 0), offset.clone().add(basisX).toLocation(start.getWorld()), 0.15, color);
                drawParticle(new Vector(0, 4, 0), offset.clone().add(basisX).add(basisZ).toLocation(start.getWorld()), 0.15, color);*/
            }
        }, 0L, 15L);

        if (schedulers.containsKey(player)) {
            schedulers.get(player).add(task);
        } else {
            List<BukkitTask> tasks = new ArrayList<>();
            tasks.add(task);
            schedulers.put(player, tasks);
        }
    }
    static final double SPACE = 0.34f;
    private static void drawParticle(Vector toDraw, Location offset, double space,Color color) {

        for(double d=0; d<=toDraw.length(); d+=space) {
            Location toSpawn = offset.clone().add(toDraw.clone().multiply(d));
            toSpawn.getWorld().spawnParticle(Particle.REDSTONE, toSpawn, 2, 0, 0, 0, 0, new Particle.DustOptions(color  , 0.7f),true);
        }
    }
}
