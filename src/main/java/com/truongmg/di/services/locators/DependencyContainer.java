package com.truongmg.di.services.locators;

import com.truongmg.di.exceptions.AlreadyInitializedException;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.instantiations.ObjectInstantiationService;

import java.lang.annotation.Annotation;
import java.util.List;

public interface DependencyContainer {

    void init(List<ServiceDetails<?>> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException;

    <T> void reload(ServiceDetails<T> serviceDetails, boolean reloadDependantServices);

    <T> T reload(T service);

    <T> T reload(T service, boolean reloadDependantServices);

    <T> T getService(Class<T> serviceType);

    <T> ServiceDetails<T> getServiceDetails(Class<T> serviceType);

    List<ServiceDetails<?>> getServicesByAnnotation(Class<? extends Annotation> annotationType);

    List<Object> getAllServices();

    List<ServiceDetails<?>> getAllServiceDetails();
}
