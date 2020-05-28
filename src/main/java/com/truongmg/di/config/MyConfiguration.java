package com.truongmg.di.config;

import com.truongmg.di.config.configurations.CustomAnnotationConfiguration;
import com.truongmg.di.config.configurations.InstantiationConfiguration;

public class MyConfiguration {

    private final CustomAnnotationConfiguration annotations;

    private final InstantiationConfiguration instantiations;

    public MyConfiguration() {
        this.annotations = new CustomAnnotationConfiguration(this);
        this.instantiations = new InstantiationConfiguration(this);
    }

    public CustomAnnotationConfiguration annotations() {
        return this.annotations;
    }

    public InstantiationConfiguration instantiations() {
        return this.instantiations;
    }

    public MyConfiguration build() {
        return this;
    }


}
