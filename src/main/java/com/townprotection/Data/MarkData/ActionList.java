package com.townprotection.Data.MarkData;

import com.townprotection.Data.ActionData.Abstract.ActionAbstract;
import com.townprotection.Data.ActionData.NormalAction;
import com.townprotection.Data.ActionData.PlayerInteract;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.luke.takoyakiLibrary.TakoUtility;

import java.util.List;

public class ActionList {
    public static ItemStack getActionItem(Action action) {
        return switch(action) {
            case PLAYER_BREAK_BLOCK -> getItemAndLore(Material.GRASS_BLOCK, "&cプレイヤーによるブロック破壊", null);
            case PLAYER_PLACE_BLOCK -> getItemAndLore(Material.GRASS_BLOCK, "&fプレイヤーによるブロック設置", null);
            case PLAYER_PVP -> getItemAndLore(Material.NETHERITE_SWORD, "&aプレイヤー同士での攻撃", null);
            case PLAYER_INTERACT -> getItemAndLore(Material.CHEST, "&dプレイヤーがチェストやかまどなどにアクセスする", List.of("&f左クリックで、ブロックごとにアクセスの許否の設定が可能です。"));

            case ENTITY_DAMAGE_TO_ENTITY -> getItemAndLore(Material.DIAMOND_SWORD, "&6エンティティによる保護内でのエンティティへの攻撃", null);

            case PISTON_MOVE_BLOCK -> getItemAndLore(Material.PISTON, "&c保護外→保護内 または 保護内→保護外へ、ピストンでブロックを移動させる", null);
            case HANGING_BREAK -> getItemAndLore(Material.PAINTING, "&d額縁・絵などの破壊", null);

            case TNT_EXPLOSION -> getItemAndLore(Material.TNT, "&cTNTによる破壊", null);
            case VEHICLE_DAMAGE -> getItemAndLore(Material.MINECART, "&c乗り物(トロッコ、ボートなど)の破壊", null);
            case VEHICLE_ENTER -> getItemAndLore(Material.MINECART, "&f乗り物(トロッコ、ボートなど)に乗る", null);
        };
    }

    //ユーティリティクラス//
    private static ItemStack getItemAndLore(Material material, String name, List<String> lore) {
        ItemStack item = TakoUtility.getItem(material, name);
        if(lore != null) {
            TakoUtility.setLore(item, lore);
        }
        return item;
    }
    ////////////////////

    public enum Action {
        PLAYER_BREAK_BLOCK,
        PLAYER_PLACE_BLOCK,
        PLAYER_INTERACT,
        PLAYER_PVP,

        ENTITY_DAMAGE_TO_ENTITY,
        PISTON_MOVE_BLOCK,

        HANGING_BREAK,
        TNT_EXPLOSION,
        VEHICLE_DAMAGE,
        VEHICLE_ENTER,
        ;

        public ActionAbstract getData() {
            if(this == Action.PLAYER_INTERACT) {
                PlayerInteract data = new PlayerInteract();

                Material[] materials = {Material.FURNACE, Material.CHEST, Material.SHULKER_BOX, Material.DISPENSER, Material.DROPPER, Material.HOPPER};

                for(Material material : materials) {
                    data.getTargets().put(material, false);
                }

                return data;
            }
            return new NormalAction();
        }
    }
}
