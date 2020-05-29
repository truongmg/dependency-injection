package com.truongmg.di.config;

import com.truongmg.di.config.configurations.ScanningConfiguration;
import com.truongmg.di.config.configurations.InstantiationConfiguration;

public class MyConfiguration {

    private final ScanningConfiguration annotations;

    private final InstantiationConfiguration instantiations;

    public MyConfiguration() {
        this.annotations = new ScanningConfiguration(this);
        this.instantiations = new InstantiationConfiguration(this);
    }

    public ScanningConfiguration scanning() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantiations;
    }

    public MyConfiguration build() {
        return this;
    }


}
