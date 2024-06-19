package com.townprotection.Effect.EffectList.System;

import com.townprotection.System.Interfaces;

public abstract class AbstractEffect implements Interfaces.Effect {
    private EffectInformation info;

    @Override
    public void setInfo(EffectInformation newInfo) {
        this.info = newInfo;
    }

    @Override
    public EffectInformation getInfo() {
        return this.info;
    }
}
