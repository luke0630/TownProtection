package com.townprotection.Data;

import com.townprotection.Data.GUIData.GUIData;
import com.townprotection.Data.MarkData.TownData;
import com.townprotection.Data.SelectorData.SelectorData;
import com.townprotection.System.RunnableSystem;
import org.bukkit.entity.Player;

import java.util.*;

public class MainData {
    public static Map<Player, SelectorData> playerSelectData = new WeakHashMap<>();

    public static List<TownData> townMarkData = new ArrayList<>(); //町のデータ

    public static Map<Player, GUIData> playerOpenGUI = new WeakHashMap<>();

    public static Map<Player, RunnableSystem.Runnable> setNameRunnable = new WeakHashMap<>();
    public static Map<Player, Integer> listPage = new WeakHashMap<>();

    public enum Filter {
        LATEST("新しい順"),
        OLDEST("古い順"),
        YOU_OWNER("あなたがオーナー")
        ;
        private final String text;

        private Filter(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }

    public enum CreateMarkedMode {
        ALL("全員"),
        OWNER_ADMIN("オーナーと管理者"),
        OWNER("オーナーのみ"),
        ;

        private final String text;

        private CreateMarkedMode(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }
}
