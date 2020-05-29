package com.truongmg.di.utils;

import com.truongmg.di.handlers.InvocationHandlerImpl;
import com.truongmg.di.handlers.MethodInvocationHandlerImpl;
import com.truongmg.di.models.ServiceBeanDetails;
import com.truongmg.di.models.ServiceDetails;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class ProxyUtils {

    public static void createProxyInstance(ServiceDetails serviceDetails, Object[] constructorParams) {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(serviceDetails.getServiceType());
        final Class<?> cls = proxyFactory.createClass();

        Object proxyInstance;
        try {
            proxyInstance = proxyFactory.create(serviceDetails.getTargetConstructor().getParameterTypes(), constructorParams);

            ((ProxyObject) proxyInstance).setHandler(new MethodInvocationHandlerImpl(serviceDetails));

            serviceDetails.setProxyInstance(proxyInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }

    public static void createBeanProxyInstance(ServiceBeanDetails serviceBeanDetails) {
        Class<?> serviceType = serviceBeanDetails.getServiceType();
        if (!serviceType.isInterface()) {
            return;
        }

        final Object proxyInstance = Proxy.newProxyInstance(
                serviceType.getClassLoader(),
                new Class[]{serviceType},
                new InvocationHandlerImpl(serviceBeanDetails)
        );

        serviceBeanDetails.setProxyInstance(proxyInstance);
    }
}
