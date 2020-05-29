package com.truongmg.di.models;

import java.util.Arrays;

public class EnqueuedServiceDetails {

    private static final String INVALID_DEPENDENCY_MSG = "Invalid dependency '%s'.";

    private final ServiceDetails serviceDetails;

    private final Class<?>[] dependencies;

    private final boolean[] dependenciesRequirement;

    private final Object[] dependencyInstances;

    public EnqueuedServiceDetails(ServiceDetails serviceDetails) {
        this.serviceDetails = serviceDetails;
        this.dependencies = serviceDetails.getTargetConstructor().getParameterTypes();
        this.dependencyInstances = new Object[this.dependencies.length];
        this.dependenciesRequirement = new boolean[this.dependencies.length];

        Arrays.fill(this.dependenciesRequirement, true);
    }

    public void addDependencyInstance(Object instance) {
        for (int i = 0; i < this.dependencies.length; i++) {
            if (this.dependencies[i].isAssignableFrom(instance.getClass())) {
                this.dependencyInstances[i] = instance;
            }
        }

    }

    public boolean isResolved() {
        for (int i = 0; i < this.dependencyInstances.length; i++) {
            if (this.dependencyInstances[i] == null && this.dependenciesRequirement[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean isDependencyRequired(Class<?> dependencyType) {
        for (Class<?> dependency : this.dependencies) {
            if (dependency.isAssignableFrom(dependencyType)) {
                return true;
            }
        }
        return false;
    }

    public ServiceDetails getServiceDetails() {
        return serviceDetails;
    }

    public Class<?>[] getDependencies() {
        return dependencies;
    }

    public Object[] getDependencyInstances() {
        return dependencyInstances;
    }

    public void setDependencyNotNull(Class<?> dependencyType, boolean isRequired) {
        for (int i = 0; i < this.dependenciesRequirement.length; i++) {
            if (this.dependencies[i].isAssignableFrom(dependencyType)) {
                this.dependenciesRequirement[i] = isRequired;
                return;
            }
        }
    }

    public boolean isDependencyNotNull(Class<?> dependencyType) {
        for (int i = 0; i < this.dependenciesRequirement.length; i++) {
            if (this.dependencies[i].isAssignableFrom(dependencyType)) {
                return this.dependenciesRequirement[i];
            }
        }
        throw new IllegalArgumentException(String.format(INVALID_DEPENDENCY_MSG, dependencyType));
    }

    @Override
    public String toString() {
        return this.serviceDetails.getServiceType().getName();
    }
}
