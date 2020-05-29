package com.truongmg.di.models;

import java.lang.reflect.Method;

public class ServiceBeanDetails extends ServiceDetails {

    private final Method originMethod;

    private final ServiceDetails rootService;

    public ServiceBeanDetails(Class<?> beanType, Method originMethod, ServiceDetails rootService) {
        this.setServiceType(beanType);
        this.setBeans(new Method[0]);
        this.originMethod = originMethod;
        this.rootService = rootService;
    }

    public Method getOriginMethod() {
        return originMethod;
    }

    public ServiceDetails getRootService() {
        return rootService;
    }

    @Override
    public Object getProxyInstance() {
        if (super.getProxyInstance() != null) {
            return super.getProxyInstance();
        }
        return this.getActualInstance();
    }

    public boolean hasProxyInstance() {
        return super.getProxyInstance() != null;
    }
}
