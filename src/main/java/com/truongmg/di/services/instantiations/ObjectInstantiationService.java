package com.truongmg.di.services.instantiations;

import com.truongmg.di.exceptions.BeanInstantiationException;
import com.truongmg.di.exceptions.ServiceInstantiationException;
import com.truongmg.di.exceptions.PreDestroyExecutionException;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;

public interface ObjectInstantiationService {

    void createInstance(ServiceDetails serviceDetails, Object... constructorParams) throws ServiceInstantiationException;

    void createBeanInstance(ServiceBeanDetails serviceBeanDetails) throws BeanInstantiationException;

    void destroyInstance(ServiceDetails serviceDetails) throws PreDestroyExecutionException;

}
