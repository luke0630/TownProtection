package com.townprotection.System;
public class RunnableSystem {

    @FunctionalInterface
    public interface Runnable {
        void run(Object data);
    }

    @FunctionalInterface
    public interface TwoRunnable {
        void run(Object data, Object data2);
    }
}
