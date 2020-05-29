package com.truongmg.di.services.instantiations;

import com.truongmg.di.models.ServiceDetails;

import java.util.Set;

public interface ServicesScanningService {

    Set<ServiceDetails> mapServices(Set<Class<?>> locatedClasses);

}
