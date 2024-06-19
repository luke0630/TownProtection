package com.townprotection.Data.MarkData;

import com.townprotection.Data.ActionData.ActionData;
import org.bukkit.Material;

public class ActionList {
    public enum Action {
        PLAYER_BREAK_BLOCK(new ActionData("&c&lプレイヤーによるブロックの破壊", Material.GRASS_BLOCK)),
        PLAYER_PLACE_BLOCK(new ActionData("&b&lプレイヤーによるブロックの設置", Material.GRASS_BLOCK)),
        ENTITY_DAMAGE_TO_ENTITY(new ActionData("&c&lエンティティーよる保護内でのエンティティへの攻撃", Material.DIAMOND_SWORD)),
        PLAYER_PVP(new ActionData("&c&lプレイヤー同士の攻撃を許可する", Material.NETHERITE_SWORD)),

        PLAYER_INTERACT(new ActionData("&c&lプレイヤーがチェストやかまどなどにアクセスする", Material.CHEST)),

        PISTON_MOVE_BLOCK(new ActionData("&d&l保護外から保護内または保護内から保護害へ、ピストンでブロックを移動できるか否か", Material.PISTON)),

        HANGING_BREAK(new ActionData("&c&l額縁や絵などの破壊", Material.PAINTING)),
        TNT_EXPLOSION(new ActionData("&c&lTNTによる地形の破壊", Material.TNT)),
        VEHICLE_DAMAGE(new ActionData("&c&lトロッコやボートなどの破壊", Material.MINECART)),
        VEHICLE_ENTER(new ActionData("&b&lトロッコやボートなどに乗る", Material.MINECART))

        ;

        final ActionData data;
        Action(ActionData name)
        {
            this.data = name;
        }
        public ActionData getData() {
            return data;
        }
    }
}
