package com.townprotection.Data.ActionData;

import com.townprotection.Data.ActionData.Abstract.ActionAbstract;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class PlayerInteract extends ActionAbstract {
    private Map<Material, Boolean> targets = new HashMap<>();

    public Map<Material, Boolean> getTargets() {
        return targets;
    }
    public void setTargets(Map<Material, Boolean> targets) {
        this.targets = targets;
    }

    public void SetTargetBoolean(Material target, Boolean result) {
        if(!targets.containsKey(target)) return;
        targets.replace(target, result);
    }

    public Boolean isAvailable(Material target) { //targets変数Mapが、trueだったらAvailable(trueがreturn)
        if(!targets.containsKey(target)) return true;
        return targets.get(target);
    }
}
