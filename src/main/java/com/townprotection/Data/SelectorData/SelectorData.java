package com.townprotection.Data.SelectorData;

import org.bukkit.Location;

public class SelectorData implements Cloneable{
    public Location startBlock = null;
    public Location endBlock = null;

    //【重要】markDataにputした後に選択範囲を変更すると、markDataにputしたデータも一緒に変更されてしまうためディープコピーを実装する //
    @Override
    public SelectorData clone() {
        try {
            SelectorData selectorData = (SelectorData) super.clone();
            selectorData.startBlock = startBlock != null ? startBlock.clone() : null;
            selectorData.endBlock = endBlock != null ? endBlock.clone() : null;
            return selectorData;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
