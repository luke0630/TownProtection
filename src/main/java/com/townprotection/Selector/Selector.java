package com.townprotection.Selector;

import com.townprotection.Data.MainData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.System.LocationRunnableSystem;
import com.townprotection.TownProtection;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Selector {
    public static Map<Player, List<BukkitTask>> schedulers = new HashMap<>();
    public static void getRange(Player player, SelectorData data, LocationRunnableSystem.LocationRunnable callBack) {
        if(player != null) {
            if(schedulers.containsKey(player)) {
                for(var schedule : schedulers.get(player)) {
                    schedule.cancel();
                }
                schedulers.remove(player); //既に含まれていたら削除しておく
            }
        }

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
            for(int i=0;i < Math.abs(xDifference);i++) {
                current.setX( current.getBlockX());

                //world.setType(current, Material.REDSTONE_BLOCK); 設置
                if(callBack != null) {
                    callBack.run(current); //コールバックを呼び出す
                }

                if(player != null) {
                    boolean b = i == Math.abs(xDifference) - 1 && a == Math.abs(zDifference) - 1;
                    if(i == 0 && a == 0) {
                        DrawParticleBlock(current, player);
                    } else if(i == Math.abs(xDifference)-1 &&  a == 0) {
                        DrawParticleBlock(current, player);
                    }
                    if(i == 0 && a == Math.abs(zDifference)-1) {
                        DrawParticleBlock(current, player);
                    } else if(b) {
                        DrawParticleBlock(current, player);
                    }
                }

                current.setX( current.getBlockX() + (-1)*Math.signum(xDifference));
            }

            current.setX(start.getBlockX());
            current.setZ( current.getBlockZ()+ (-1)*Math.signum(zDifference));
        }
    }
    static void DrawParticleBlock(Location start, Player player) {
        Vector offset = start.clone().toVector();

        Vector basisX = new Vector(1, 0, 0);
        Vector basisY = new Vector(0, 1, 0);
        Vector basisZ = new Vector(0, 0, 1);

        var task = Bukkit.getScheduler().runTaskTimer(TownProtection.instance, () -> {
            drawParticle(basisY, offset.clone().toLocation(start.getWorld()), SPACE);
            drawParticle(basisY, offset.clone().add(basisX).toLocation(start.getWorld()), SPACE);
            drawParticle(basisY, offset.clone().add(basisX).add(basisZ).toLocation(start.getWorld()), SPACE);
            drawParticle(basisY, offset.clone().add(basisZ).toLocation(start.getWorld()), SPACE);

            drawParticle(basisX, offset.clone().toLocation(start.getWorld()), SPACE);
            drawParticle(basisX, offset.clone().add(basisY).toLocation(start.getWorld()), SPACE);
            drawParticle(basisX, offset.clone().add(basisZ).toLocation(start.getWorld()), SPACE);
            drawParticle(basisX, offset.clone().add(basisY).add(basisZ).toLocation(start.getWorld()), SPACE);

            drawParticle(basisZ, offset.clone().toLocation(start.getWorld()), SPACE);
            drawParticle(basisZ, offset.clone().add(basisX).toLocation(start.getWorld()), SPACE);
            drawParticle(basisZ, offset.clone().add(basisY).toLocation(start.getWorld()), SPACE);
            drawParticle(basisZ, offset.clone().add(basisY).add(basisX).toLocation(start.getWorld()), SPACE);

            // Optional: Draw particles above the block
            drawParticle(new Vector(0, 10, 0), offset.clone().toLocation(start.getWorld()), 0.05);
            drawParticle(new Vector(0, 10, 0), offset.clone().add(basisZ).toLocation(start.getWorld()), 0.05);
            drawParticle(new Vector(0, 10, 0), offset.clone().add(basisX).toLocation(start.getWorld()), 0.05);
            drawParticle(new Vector(0, 10, 0), offset.clone().add(basisX).add(basisZ).toLocation(start.getWorld()), 0.05);
        }, 0L, 15L);

        if (schedulers.containsKey(player)) {
            schedulers.get(player).add(task);
        } else {
            List<BukkitTask> tasks = new ArrayList<>();
            tasks.add(task);
            schedulers.put(player, tasks);
        }
    }
    static final double SPACE = 0.08f;
    private static void drawParticle(Vector toDraw, Location offset, double space) {

        for(double d=0; d<=toDraw.length(); d+=space) {
            Location toSpawn = offset.clone().add(toDraw.clone().multiply(d));
            toSpawn.getWorld().spawnParticle(Particle.REDSTONE, toSpawn, 2, 0, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 0.7f),true);
        }
    }
}
