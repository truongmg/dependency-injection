package com.truongmg.di.utils;

import com.truongmg.di.models.ServiceDetails;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.InvocationTargetException;

public class ProxyUtils {

    public static void createProxyInstance(ServiceDetails serviceDetails, Object[] constructorParams) {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setSuperclass(serviceDetails.getServiceType());
        final Class<?> cls = proxyFactory.createClass();

        Object proxyInstance;
        try {
            proxyInstance = cls.getDeclaredConstructors()[0].newInstance(constructorParams);
            ((ProxyObject) proxyInstance).setHandler(
                    (self, thisMethod, proceed, args) -> thisMethod.invoke(serviceDetails.getActualInstance(), args)
            );
            serviceDetails.setProxyInstance(proxyInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }


    }
}
