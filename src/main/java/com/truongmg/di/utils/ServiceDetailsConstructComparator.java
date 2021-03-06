package com.truongmg.di.utils;

import com.truongmg.di.models.ServiceDetails;

import java.util.Comparator;

public class ServiceDetailsConstructComparator implements Comparator<ServiceDetails> {

    @Override
    public int compare(ServiceDetails sd1, ServiceDetails sd2) {
        if (sd1.getTargetConstructor() == null || sd2.getTargetConstructor() == null) {
            return 0;
        }

        return Integer.compare(
                sd1.getTargetConstructor().getParameterCount(), sd2.getTargetConstructor().getParameterCount()
        );
    }

}
