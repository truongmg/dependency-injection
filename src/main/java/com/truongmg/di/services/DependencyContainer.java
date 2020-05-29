package com.truongmg.di.services;

import com.truongmg.di.exceptions.AlreadyInitializedException;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.instantiations.ObjectInstantiationService;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

public interface DependencyContainer {

    void init(Collection<Class<?>> locatedClasses, Collection<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException;

    void reload(ServiceDetails serviceDetails);

    void reload(Class<?> serviceType);

    <T> T getService(Class<T> serviceType);

    ServiceDetails getServiceDetails(Class<?> serviceType);

    Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    Collection<ServiceDetails> getAllServices();

    Collection<ServiceDetails> getImplementations(Class<?> serviceType);

    Collection<Class<?>> getAllScannedClasses();

    void update(Object service);

    void update(Class<?> serviceType, Object serviceInstance);
}
