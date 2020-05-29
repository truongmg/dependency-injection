package com.truongmg.di.services.instantiations;

import com.truongmg.di.config.configurations.InstantiationConfiguration;
import com.truongmg.di.exceptions.ServiceInstantiationException;
import com.truongmg.di.models.EnqueuedServiceDetails;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.utils.ProxyUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ServicesInstantiationServiceImpl implements ServicesInstantiationService {

    private static final String MAX_NUMBER_OF_ALLOWED_ITERATION_REACH_MSG = "Maximum number of allowed iterations was reached '%s'";
    private static final String NO_CONSTRUCTOR_PARAM_MSG = "Could not create instance of '%s', Parameter '%s' implementation was not found";

    private final InstantiationConfiguration configuration;

    private final ObjectInstantiationService instantiationService;

    private final LinkedList<EnqueuedServiceDetails> enqueuedServiceDetails;

    private final List<Class<?>> allAvailableClasses;

    private final List<ServiceDetails> instantiatedServices;

    public ServicesInstantiationServiceImpl(InstantiationConfiguration configuration, ObjectInstantiationService instantiationService) {
        this.configuration = configuration;
        this.instantiationService = instantiationService;
        this.enqueuedServiceDetails = new LinkedList<>();
        this.allAvailableClasses = new ArrayList<>();
        this.instantiatedServices = new ArrayList<>();
    }

    @Override
    public List<ServiceDetails> instantiateServicesAndBeans(Set<ServiceDetails> mappedServices) throws ServiceInstantiationException {
        this.init(mappedServices);
        this.checkForMissingServices(mappedServices);

        int counter = 0;
        int maximumAllowedIterations = this.configuration.getMaximumAllowedIterations();
        while (!this.enqueuedServiceDetails.isEmpty()) {
            if (counter > maximumAllowedIterations) {
                throw new ServiceInstantiationException(String.format(MAX_NUMBER_OF_ALLOWED_ITERATION_REACH_MSG, maximumAllowedIterations));
            }

            EnqueuedServiceDetails enqueuedServiceDetails = this.enqueuedServiceDetails.removeFirst();
            if (enqueuedServiceDetails.isResolved()) {
                ServiceDetails serviceDetails = enqueuedServiceDetails.getServiceDetails();
                Object[] dependencyInstanced = enqueuedServiceDetails.getDependencyInstanced();

                this.instantiationService.createInstance(serviceDetails, dependencyInstanced);
                ProxyUtils.createProxyInstance(serviceDetails, enqueuedServiceDetails.getDependencyInstanced());

                this.registerInstantiatedService(serviceDetails);
                this.registerBeans(serviceDetails);
            } else {
                this.enqueuedServiceDetails.addLast(enqueuedServiceDetails);
                counter++;
            }
        }

        return instantiatedServices;
    }

    private void registerBeans(ServiceDetails serviceDetails) {
        for (Method beanMethod : serviceDetails.getBeans()) {
            ServiceBeanDetails beanDetails = new ServiceBeanDetails(beanMethod.getReturnType(), beanMethod, serviceDetails);
            this.instantiationService.createBeanInstance(beanDetails);
            beanDetails.setProxyInstance(beanDetails.getActualInstance());

            this.registerInstantiatedService(beanDetails);
        }
    }

    private void registerInstantiatedService(ServiceDetails newlyCreatedService) {

        if (!(newlyCreatedService instanceof ServiceBeanDetails)) {
            this.updateDependantServices(newlyCreatedService);
        }

        this.instantiatedServices.add(newlyCreatedService);

        for (EnqueuedServiceDetails enqueuedService : this.enqueuedServiceDetails) {
            if (enqueuedService.isDependencyRequired(newlyCreatedService.getServiceType())) {
                enqueuedService.addDependencyInstance(newlyCreatedService.getProxyInstance());
            }
        }
    }

    private void updateDependantServices(ServiceDetails newService) {
        for (Class<?> parameterType : newService.getTargetConstructor().getParameterTypes()) {
            for (ServiceDetails serviceDetails : this.instantiatedServices) {
                if (parameterType.isAssignableFrom(serviceDetails.getServiceType())) {
                    serviceDetails.addDependantService(newService);
                }
            }
        }
    }

    private void checkForMissingServices(Set<ServiceDetails> mappedServices) {
        for (ServiceDetails serviceDetails : mappedServices) {
            for (Class<?> parameterType : serviceDetails.getTargetConstructor().getParameterTypes()) {
                if (!this.isAssignableTypePresent(parameterType)) {
                    throw new ServiceInstantiationException(
                            String.format(NO_CONSTRUCTOR_PARAM_MSG, serviceDetails.getServiceType().getName(), parameterType.getName())
                    );
                }
            }
        }
    }

    private boolean isAssignableTypePresent(Class<?> cls) {
        for (Class<?> serviceType : this.allAvailableClasses) {
            if (cls.isAssignableFrom(serviceType)) {
                return true;
            }
        }

        return false;
    }

    private void init(Set<ServiceDetails> mappedServices) {
        this.enqueuedServiceDetails.clear();
        this.allAvailableClasses.clear();
        this.instantiatedServices.clear();

        for (ServiceDetails serviceDetails : mappedServices) {
            this.enqueuedServiceDetails.add(new EnqueuedServiceDetails(serviceDetails));
            this.allAvailableClasses.add(serviceDetails.getServiceType());
            this.allAvailableClasses.addAll(
                    Arrays.stream(serviceDetails.getBeans())
                            .map(Method::getReturnType)
                            .collect(Collectors.toList())
            );
        }

    }

}
