package com.truongmg.di.services;

import com.truongmg.di.enums.ScopeType;
import com.truongmg.di.exceptions.AlreadyInitializedException;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.instantiations.ObjectInstantiationService;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyContainerImpl implements DependencyContainer {

    private static final String ALREADY_INITIALIZED_MSG = "Dependency Container already initialized";
    private static final String SERVICE_NOT_FOUND_MSG = "";
    private boolean isInit;
    private List<ServiceDetails> servicesAndBeans;
    private ObjectInstantiationService instantiationService;
    private Collection<Class<?>> locatedClasses;

    public DependencyContainerImpl() {
        this.isInit = false;
    }

    @Override
    public void init(Collection<Class<?>> locatedClasses, List<ServiceDetails> servicesAndBeans, ObjectInstantiationService instantiationService) throws AlreadyInitializedException {
        if (this.isInit) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED_MSG);
        }

        this.locatedClasses = locatedClasses;
        this.servicesAndBeans = servicesAndBeans;
        this.instantiationService = instantiationService;
        this.isInit = true;

    }

    @Override
    public void reload(ServiceDetails serviceDetails, boolean reloadDependantServices) {
        this.instantiationService.destroyInstance(serviceDetails);
        this.handleReload(serviceDetails);

        if (reloadDependantServices) {
            for (ServiceDetails dependantService : serviceDetails.getDependantServices()) {
                this.reload(dependantService, reloadDependantServices);
            }
        }
    }

    private void handleReload(ServiceDetails serviceDetails) {
        if (serviceDetails instanceof ServiceBeanDetails) {
            this.instantiationService.createBeanInstance((ServiceBeanDetails) serviceDetails);
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
    public <T> T reload(T service) {
        return this.reload(service, false);
    }

    @Override
    public <T> T reload(T service, boolean reloadDependantServices) {
        final ServiceDetails serviceDetails = this.getServiceDetails(service.getClass());
        if (serviceDetails == null) return null;

        this.reload(serviceDetails, reloadDependantServices);
        return (T) serviceDetails.getActualInstance();
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        final ServiceDetails serviceDetails = this.getServiceDetails(serviceType);
        if (serviceDetails != null) return (T) serviceDetails.getActualInstance();
        return null;
    }

    @Override
    public ServiceDetails getServiceDetails(Class<?> serviceType) {
        ServiceDetails serviceDetails = findServiceDetails(serviceType);

        if (serviceDetails != null) {
            if (serviceDetails.getScopeType() == ScopeType.PROTOTYPE) {
                serviceDetails.setInstance(this.getNewInstance(serviceType));
            }
        }


        return serviceDetails;
    }

    private <T> ServiceDetails findServiceDetails(Class<T> serviceType) {
        return this.servicesAndBeans.stream()
                    .filter(sd -> serviceType.isAssignableFrom(sd.getServiceType()))
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
    public List<ServiceDetails> getServicesByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream()
                .filter(sd -> sd.getAnnotations().contains(annotationType))
                .collect(Collectors.toList());
    }

    @Override
    public List<Object> getAllServices() {
        return this.servicesAndBeans.stream()
                .map(ServiceDetails::getProxyInstance)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceDetails> getAllServiceDetails() {
        return Collections.unmodifiableList(this.servicesAndBeans);
    }

    @Override
    public Collection<Class<?>> getLocatedClasses() {
        return Collections.unmodifiableCollection(this.locatedClasses);
    }
}
