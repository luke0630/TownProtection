package com.townprotection.GUI;

public class GuiManager {
    public enum GUi {
        PLAYER_LIST,
        CHANGE_MAYOR,
        APPLY_SELECTOR_DATA,
        MARK_DATA_EDITOR,
        MARK_DATA_DELETE,
        TOWN_EDITOR,
        TOWN_EFFECT_EDITOR,
        TOWN_DELETE_CONFIRM,
        TOWN_ICON_MODE_SELECT,
    }

    public enum ListGUIPreset {
        TOWN_LIST,
        TOWN_EFFECT,
        TOWN_EFFECT_LIST,
        TOWN_MANAGER_LIST,
        TOWN_MANAGER_ADD,
        TOWN_MARKED_LIST,
        TOWN_ICON_BLOCK_LIST,
        TOWN_ICON_PLAYER_INVENTORY_LIST,
        ACTION_LIST,
        ACTION_CURRENT_LIST,
        MANAGER_ADD_LIST,
        MANAGER_CURRENT_LIST,
        ALLOWED_CURRENT_PLAYER_LIST,
        ALLOWED_ADD_PLAYER_LIST,
        SELECT_MAYOR,
        PlayerInteractGUI,
    }
}
