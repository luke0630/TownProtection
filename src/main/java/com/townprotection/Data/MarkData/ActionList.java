package com.townprotection.Data.MarkData;

public class ActionList {
    public enum Action {
        PLAYER_BREAK_BLOCK("プレイヤーによるブロックの破壊"),
        PLAYER_PLACE_BLOCK("プレイヤーによるブロックの設置"),
        PLAYER_DAMAGE_TO_ENTITY("プレイヤーによる保護内でのエンティティ(プレイヤーも含む)への攻撃"),

        PLAYER_INTERACT("プレイヤーがチェストやかまどなどにアクセスする"),

        HANGING_BREAK("額縁や絵などの破壊"),
        TNT_EXPLOSION("TNTによる地形の破壊"),
        VEHICLE_DAMAGE("トロッコやボートなどの破壊"),
        VEHICLE_ENTER("トロッコやボートなどに乗る");



        private final String name;

        Action(String name)
        {
            this.name = name;
        }
    }
}
