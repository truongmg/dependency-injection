package com.truongmg.di.config;

public abstract class BaseSubConfiguration {

    private final MyConfiguration parentConfig;

    protected BaseSubConfiguration(MyConfiguration parentConfig) {
        this.parentConfig = parentConfig;
    }

    public MyConfiguration and() {
        return this.parentConfig;
    }

}
