package com.truongmg.di.config.configurations;

import com.truongmg.di.config.BaseSubConfiguration;
import com.truongmg.di.config.MyConfiguration;
import com.truongmg.di.constants.Constants;

public class InstantiationConfiguration extends BaseSubConfiguration {

    private int maximumAllowedIterations;

    public InstantiationConfiguration(MyConfiguration parentConfig) {
        super(parentConfig);
        this.maximumAllowedIterations = Constants.MAX_NUMBER_OF_INSTANTIATION_ITERATIONS;
    }

    public InstantiationConfiguration setMaximumNumberOfAllowedIterations(int num) {
        this.maximumAllowedIterations = num;
        return this;
    }

    public int getMaximumAllowedIterations() {
        return maximumAllowedIterations;
    }
}
