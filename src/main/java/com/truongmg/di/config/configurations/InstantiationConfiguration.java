package com.truongmg.di.config.configurations;

import com.truongmg.di.config.BaseSubConfiguration;
import com.truongmg.di.config.MyConfiguration;
import com.truongmg.di.constants.Constants;
import com.truongmg.di.models.ServiceDetails;

import java.util.ArrayList;
import java.util.Collection;

public class InstantiationConfiguration extends BaseSubConfiguration {

    private int maximumAllowedIterations;

    private final Collection<ServiceDetails> providedServices;

    public InstantiationConfiguration(MyConfiguration parentConfig) {
        super(parentConfig);
        this.maximumAllowedIterations = Constants.MAX_NUMBER_OF_INSTANTIATION_ITERATIONS;
        this.providedServices = new ArrayList<>();
    }

    public InstantiationConfiguration setMaximumNumberOfAllowedIterations(int num) {
        this.maximumAllowedIterations = num;
        return this;
    }

    public int getMaximumAllowedIterations() {
        return maximumAllowedIterations;
    }

    public InstantiationConfiguration addProvidedServices(Collection<ServiceDetails> serviceDetails) {
        this.providedServices.addAll(serviceDetails);
        return this;
    }

    public Collection<ServiceDetails> getProvidedServices() {
        return this.providedServices;
    }
}
