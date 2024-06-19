package com.townprotection.System;

import com.townprotection.Effect.EffectList.System.EffectInformation;

public class Interfaces {
    public interface Effect {
        void run();
        void setInfo(EffectInformation newInfo);
        EffectInformation getInfo();
    }
}
