package com.truongmg.di.models;

import com.truongmg.di.enums.ScopeType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ServiceDetails {

    private static final String PROXY_ALREADY_CREATED_MSG = "Proxy instance already created.";

    private Class<?> serviceType;

    private List<Class<? extends Annotation>> annotations;

    private Constructor<?> targetConstructor;

    private Object instance;

    private Object proxyInstance;

    private Method postConstructMethod;

    private Method preDestroyMethod;

    private Method[] beans;

    private final List<ServiceDetails> dependantServices;

    private ScopeType scopeType;

    public ServiceDetails() {
        this.dependantServices = new ArrayList<>();
        this.annotations = new ArrayList<>();
    }

    public ServiceDetails(Class<?> serviceType, Collection<Class<? extends Annotation>> annotations, Constructor<?> targetConstructor,
                          Method postConstructMethod, Method preDestroyMethod, Method[] beans, ScopeType scopeType) {
        this();
        this.serviceType = serviceType;
        this.addAnnotations(annotations);
        this.targetConstructor = targetConstructor;
        this.postConstructMethod = postConstructMethod;
        this.preDestroyMethod = preDestroyMethod;
        this.beans = beans;
        this.scopeType = scopeType;
    }

    public Class<?> getServiceType() {
        return serviceType;
    }

    public void setServiceType(Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(Class<? extends Annotation> annotation) {
        this.annotations.add(annotation);
    }

    public void addAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.annotations.addAll(annotations);
    }

    public Constructor<?> getTargetConstructor() {
        return targetConstructor;
    }

    public void setTargetConstructor(Constructor<?> targetConstructor) {
        this.targetConstructor = targetConstructor;
    }

    public Object getActualInstance() {
        return instance;
    }

    public Object getProxyInstance() {
        return proxyInstance;
    }

    public void setProxyInstance(Object proxyInstance) {
        if (this.proxyInstance != null) {
            throw new IllegalArgumentException(PROXY_ALREADY_CREATED_MSG);
        }
        this.proxyInstance = proxyInstance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getPostConstructMethod() {
        return postConstructMethod;
    }

    public void setPostConstructMethod(Method postConstructMethod) {
        this.postConstructMethod = postConstructMethod;
    }

    public Method[] getBeans() {
        return beans;
    }

    public void setBeans(Method[] beans) {
        this.beans = beans;
    }

    public List<ServiceDetails> getDependantServices() {
        return Collections.unmodifiableList(this.dependantServices);
    }

    public void addDependantService(ServiceDetails serviceDetail) {
        this.dependantServices.add(serviceDetail);
    }

    @Override
    public int hashCode() {
        if (this.serviceType == null) {
            return super.hashCode();
        }
        return this.serviceType.hashCode();
    }

    public Method getPreDestroyMethod() {
        return preDestroyMethod;
    }

    public void setPreDestroyMethod(Method preDestroyMethod) {
        this.preDestroyMethod = preDestroyMethod;
    }

    @Override
    public String toString() {
        if (serviceType.getName() == null) {
            return super.toString();
        }
        return serviceType.getName();
    }

    public ScopeType getScopeType() {
        return this.scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }
}
