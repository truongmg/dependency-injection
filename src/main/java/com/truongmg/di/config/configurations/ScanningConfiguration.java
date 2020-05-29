package com.truongmg.di.config.configurations;

import com.truongmg.di.config.BaseSubConfiguration;
import com.truongmg.di.config.MyConfiguration;

import java.lang.annotation.Annotation;
import java.util.*;

public class ScanningConfiguration extends BaseSubConfiguration {

    private final Set<Class<? extends Annotation>> customServiceAnnotations;

    private final Set<Class<? extends Annotation>> customBeanAnnotations;

    private final Map<Class<?>, Class<? extends Annotation>> additionalClasses;

    private ClassLoader classLoader;

    public ScanningConfiguration(MyConfiguration parentConfig) {
        super(parentConfig);
        this.additionalClasses = new HashMap<>();
        this.customServiceAnnotations = new HashSet<>();
        this.customBeanAnnotations = new HashSet<>();
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ScanningConfiguration addCustomServiceAnnotation(Class<? extends Annotation> annotation) {
        this.customServiceAnnotations.add(annotation);
        return this;
    }

    public ScanningConfiguration addCustomServiceAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customServiceAnnotations.addAll(new HashSet<>(annotations));
        return this;
    }

    public ScanningConfiguration addCustomBeanAnnotation(Class<? extends Annotation> annotation) {
        this.customBeanAnnotations.add(annotation);
        return this;
    }

    public ScanningConfiguration addCustomBeanAnnotations(Collection<Class<? extends Annotation>> annotations) {
        this.customBeanAnnotations.addAll(new HashSet<>(annotations));
        return this;
    }

    public Set<Class<? extends Annotation>> getCustomServiceAnnotations() {
        return this.customServiceAnnotations;
    }

    public Set<Class<? extends Annotation>> getCustomBeanAnnotations() {
        return this.customBeanAnnotations;
    }

    public Map<Class<?>, Class<? extends Annotation>> getAdditionalClasses() {
        return this.additionalClasses;
    }

    public ScanningConfiguration addAdditionalClassesForScanning(Map<Class<?>, Class<? extends Annotation>> additionalClasses) {
        this.additionalClasses.putAll(additionalClasses);
        return this;
    }

    public ScanningConfiguration setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

}
