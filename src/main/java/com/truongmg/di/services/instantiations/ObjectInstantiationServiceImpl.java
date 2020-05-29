package com.truongmg.di.services.instantiations;

import com.truongmg.di.exceptions.BeanInstantiationException;
import com.truongmg.di.exceptions.PostConstructionException;
import com.truongmg.di.exceptions.ServiceInstantiationException;
import com.truongmg.di.exceptions.PreDestroyExecutionException;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ObjectInstantiationServiceImpl implements ObjectInstantiationService {

    private static final String INVALID_PARAMETERS_COUNT_MSG = "Invalid parameters count for '%s'.";

    @Override
    public void createInstance(ServiceDetails serviceDetails, Object... constructorParams) throws ServiceInstantiationException {
        final Constructor<?> targetConstructor = serviceDetails.getTargetConstructor();

        if (constructorParams.length != targetConstructor.getParameterCount()) {
            throw new ServiceInstantiationException(String.format(INVALID_PARAMETERS_COUNT_MSG, serviceDetails.getServiceType().getName()));
        }

        try {
            final Object instance = targetConstructor.newInstance(constructorParams);
            serviceDetails.setInstance(instance);
            this.callPostConstruct(serviceDetails);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ServiceInstantiationException(e.getMessage(), e);
        }

    }

    private void callPostConstruct(ServiceDetails serviceDetails) {
        if (serviceDetails.getPostConstructMethod() == null) {
            return;
        }

        try {
            serviceDetails.getPostConstructMethod().invoke(serviceDetails.getActualInstance());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PostConstructionException(e.getMessage(), e);
        }

    }

    @Override
    public void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException {
        final Method originMethod = serviceBeanDetails.getOriginMethod();
        final Object rootInstance = serviceBeanDetails.getRootService().getActualInstance();

        try {
            final Object instance = originMethod.invoke(rootInstance);
            serviceBeanDetails.setInstance(instance);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BeanInstantiationException(e.getMessage(), e);
        }
    }

    @Override
    public void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException {
        if (serviceDetails.getPreDestroyMethod() != null) {
            try {
                serviceDetails.getPreDestroyMethod().invoke(serviceDetails.getActualInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new PreDestroyExecutionException(e.getMessage(), e);
            }
        }
        serviceDetails.setInstance(null);
    }
}
