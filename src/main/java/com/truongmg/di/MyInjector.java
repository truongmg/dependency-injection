package com.truongmg.di;

import com.truongmg.di.annotations.Service;
import com.truongmg.di.annotations.StartUp;
import com.truongmg.di.config.MyConfiguration;
import com.truongmg.di.enums.DirectoryType;
import com.truongmg.di.models.Directory;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.instantiations.*;
import com.truongmg.di.services.locators.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Service
public class MyInjector {

    public static final DependencyContainer dependencyContainer;

    static {
        dependencyContainer = new DependencyContainerImpl();
    }

    public static void main(String[] args) {
        run(MyInjector.class);
    }


    public static void run(Class<?> startupClass) {
        run(startupClass, new MyConfiguration());
    }

    public static void run(Class<?> startupClass, MyConfiguration configuration) {
        ServicesScanningService scanningService = new ServicesScanningServiceImpl(configuration.annotations());
        ObjectInstantiationService objectInstantiationService = new ObjectInstantiationServiceImpl();
        ServicesInstantiationService instantiationService = new ServicesInstantiationServiceImpl(configuration.instantiations(), objectInstantiationService);
        Directory directory = new DirectoryResolverImpl().resolveDirectory(startupClass);

        ClassLocator classLocator = new ClassLocatorForDirectory();
        if (directory.getDirectoryType() == DirectoryType.JAR_FILE) {
            classLocator = new ClassLocatorForJarFile();
        }

        Set<Class<?>> locateClasses = classLocator.locateClasses(directory.getDirectory());

        Set<ServiceDetails<?>> mappedServices = scanningService.mapServices(locateClasses);
        List<ServiceDetails<?>> serviceDetails = instantiationService.instantiateServicesAndBeans(mappedServices);

        dependencyContainer.init(serviceDetails, objectInstantiationService);
        runStartUpMethod(startupClass);
    }

    private static void runStartUpMethod(Class<?> startupClass) {
        ServiceDetails<?> serviceDetails = dependencyContainer.getServiceDetails(startupClass);
        for (Method declaredMethod : serviceDetails.getServiceType().getDeclaredMethods()) {
            if (declaredMethod.getParameterCount() != 0 || (declaredMethod.getReturnType() != void.class && declaredMethod.getReturnType() != Void.class) || !declaredMethod.isAnnotationPresent(StartUp.class)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            try {
                declaredMethod.invoke(serviceDetails.getInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
