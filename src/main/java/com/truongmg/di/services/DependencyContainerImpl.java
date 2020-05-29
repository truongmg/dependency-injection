package com.truongmg.di.services;

import com.truongmg.di.enums.ScopeType;
import com.truongmg.di.exceptions.AlreadyInitializedException;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.instantiations.ObjectInstantiationService;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class DependencyContainerImpl implements DependencyContainer {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency Container already initialized";
    private static final String SERVICE_NOT_FOUND_MSG = "Service '%s' was not found.";
    private boolean isInit;
    private Collection<ServiceDetails> servicesAndBeans;
    private ObjectInstantiationService instantiationService;
    private Collection<Class<?>> allLocatedClasses;
    private final Map<Class<?>, ServiceDetails> cachedServices;
    private final Map<Class<?>, Collection<ServiceDetails>> cachedImplementations;
    private final Map<Class<? extends Annotation>, Collection<ServiceDetails>> cachedServicesByAnnotation;


    public DependencyContainerImpl() {
        this.isInit = false;
        cachedServices = new HashMap<>();
        cachedImplementations = new HashMap<>();
        cachedServicesByAnnotation = new HashMap<>();
    }

    @Override
    public void init(Collection<Class<?>> locatedClasses, Collection<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException {
        if (this.isInit) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED_MSG);
        }

        this.allLocatedClasses = locatedClasses;
        this.servicesAndBeans = servicesAndBeans;
        this.instantiationService = instantiationService;
        this.isInit = true;

    }

    @Override
    public void reload(ServiceDetails serviceDetails) {
        this.instantiationService.destroyInstance(serviceDetails);
        this.handleReload(serviceDetails);
    }

    private void handleReload(ServiceDetails serviceDetails) {
        if (serviceDetails instanceof ServiceBeanDetails) {
            ServiceBeanDetails serviceBeanDetails = (ServiceBeanDetails) serviceDetails;
            this.instantiationService.createBeanInstance(serviceBeanDetails);

            if (!serviceBeanDetails.hasProxyInstance()) {
                for (ServiceDetails dependantService : serviceDetails.getDependantServices()) {
                    this.reload(dependantService);
                }
            }
        } else {
            this.instantiationService.createInstance(serviceDetails, this.collectDependencies(serviceDetails));
        }
    }

    private Object[] collectDependencies(ServiceDetails serviceDetails) {
        final Class<?>[] parameterTypes = serviceDetails.getTargetConstructor().getParameterTypes();
        final Object[] dependencyInstances = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            dependencyInstances[i] = this.getService(parameterTypes[i]);
        }

        return dependencyInstances;
    }

    @Override
    public void reload(Class<?> serviceType) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_MSG, serviceType));
        }

        this.reload(serviceDetails);
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails != null) return (T) serviceDetails.getProxyInstance();
        return null;
    }

    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType) {
        if (this.cachedServices.containsKey(serviceType)) {
            return this.cachedServices.get(serviceType);
        }

        ServiceDetails serviceDetails = findServiceDetails(serviceType);
        if (serviceDetails != null) {
            this.cachedServices.put(serviceType, serviceDetails);
        }

        return serviceDetails;
    }

    private <T> ServiceDetails findServiceDetails(Class<T> serviceType) {
        return this.servicesAndBeans.stream()
                    .filter(sd -> serviceType.isAssignableFrom(sd.getProxyInstance().getClass()) || serviceType.isAssignableFrom(sd.getServiceType()))
                    .findFirst()
                    .orElse(null);
    }

    private <T> Object getNewInstance(Class<T> serviceType) {
        ServiceDetails serviceDetails = findServiceDetails(serviceType);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_MSG, serviceType.getName()));
        }

        if (serviceDetails instanceof ServiceBeanDetails) {
            this.instantiationService.createBeanInstance((ServiceBeanDetails) serviceDetails);
        } else {
            this.instantiationService.createInstance(serviceDetails, this.collectDependencies(serviceDetails));
        }

        return serviceDetails.getActualInstance();
    }

    @Override
    public Collection<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        if (cachedServicesByAnnotation.containsKey(annotationType)) {
            return cachedServicesByAnnotation.get(annotationType);
        }

        List<ServiceDetails> servicesByAnnotation = this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotation() != null && sd.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());

        this.cachedServicesByAnnotation.put(annotationType, servicesByAnnotation);
        return servicesByAnnotation;
    }

    @Override
    public Collection<ServiceDetails> getImplementations(Class<?> serviceType) {
        if (this.cachedImplementations.containsKey(serviceType)) {
            return this.cachedImplementations.get(serviceType);
        }

        List<ServiceDetails> implementations = this.servicesAndBeans.stream()
                .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
                .collect(Collectors.toList());

        this.cachedImplementations.put(serviceType, implementations);
        return implementations;
    }

    @Override
    public Collection<ServiceDetails> getAllServices() {
        return this.servicesAndBeans;
    }

    @Override
    public Collection<Class<?>> getAllScannedClasses() {
        return this.allLocatedClasses;
    }

    @Override
    public void update(Object service) {
        this.update(service.getClass(), service);

    }

    @Override
    public void update(Class<?> serviceType, Object serviceInstance) {
        ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails == null) {
            throw new IllegalArgumentException(String.format(SERVICE_NOT_FOUND_MSG, serviceType.getName()));
        }
        this.instantiationService.destroyInstance(serviceDetails);
        serviceDetails.setInstance(serviceInstance);
    }
}
