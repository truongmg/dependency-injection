package com.truongmg.di.services.instantiations;

import com.truongmg.di.exceptions.ServiceInstantiationException;
import com.truongmg.di.models.ServiceDetails;

import java.util.List;
import java.util.Set;

public interface ServicesInstantiationService {

    List<ServiceDetails<?>> instantiateServicesAndBeans(Set<ServiceDetails<?>> mappedServices) throws ServiceInstantiationException;

}
