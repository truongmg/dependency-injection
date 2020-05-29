package com.truongmg.di;

import com.truongmg.di.annotations.Service;
import com.truongmg.di.annotations.StartUp;
import com.truongmg.di.config.MyConfiguration;
import com.truongmg.di.enums.DirectoryType;
import com.truongmg.di.models.Directory;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.services.DependencyContainer;
import com.truongmg.di.services.DependencyContainerImpl;
import com.truongmg.di.services.instantiations.*;
import com.truongmg.di.services.locators.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MyInjector {

    public static void main(String[] args) {
        run(MyInjector.class);
    }


    public static DependencyContainer run(Class<?> startupClass) {
        return run(startupClass, new MyConfiguration());
    }

    public static DependencyContainer run(Class<?> startupClass, MyConfiguration configuration) {
        final String directory = new DirectoryResolverImpl().resolveDirectory(startupClass).getDirectory();
        final DependencyContainer dependencyContainer = run(new File[]{new File(directory)}, configuration);

        runStartUpMethod(startupClass, dependencyContainer);

        return dependencyContainer;
    }

    public static DependencyContainer run(File[] startupDirectories, MyConfiguration configuration) {
        final ServicesScanningService scanningService = new ServicesScanningServiceImpl(configuration.scanning());
        final ObjectInstantiationService objectInstantiationService = new ObjectInstantiationServiceImpl();
        final ServicesInstantiationService instantiationService = new ServicesInstantiationServiceImpl(configuration.instantiations(), objectInstantiationService);

        final Set<Class<?>> locateClasses = locateClasses(startupDirectories, configuration);
        final Set<ServiceDetails> mappedServices = scanningService.mapServices(locateClasses);
        final List<ServiceDetails> serviceDetails = instantiationService.instantiateServicesAndBeans(mappedServices);

        final DependencyContainer dependencyContainer = new DependencyContainerImpl();
        dependencyContainer.init(locateClasses, serviceDetails, objectInstantiationService);
        return dependencyContainer;
    }

    private static Set<Class<?>> locateClasses(File[] startupDirectories, MyConfiguration configuration) {
        final Set<Class<?>> locatedClasses = new HashSet<>();
        final DirectoryResolver directoryResolver = new DirectoryResolverImpl();

        for (File startupDirectory : startupDirectories) {
            final Directory directory = directoryResolver.resolveDirectory(startupDirectory);

            ClassLocator classLocator = new ClassLocatorForDirectory(configuration);
            if (directory.getDirectoryType() == DirectoryType.JAR_FILE) {
                classLocator = new ClassLocatorForJarFile(configuration);
            }

            locatedClasses.addAll(classLocator.locateClasses(directory.getDirectory()));
        }
        return locatedClasses;
    }

    private static void runStartUpMethod(Class<?> startupClass, DependencyContainer dependencyContainer) {
        final ServiceDetails serviceDetails = dependencyContainer.getServiceDetails(startupClass);
        if (serviceDetails == null) {
            return;
        }
        for (Method declaredMethod : serviceDetails.getServiceType().getDeclaredMethods()) {
            if (declaredMethod.getParameterCount() != 0 || (declaredMethod.getReturnType() != void.class && declaredMethod.getReturnType() != Void.class) || !declaredMethod.isAnnotationPresent(StartUp.class)) {
                continue;
            }

            declaredMethod.setAccessible(true);
            try {
                declaredMethod.invoke(serviceDetails.getActualInstance());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
