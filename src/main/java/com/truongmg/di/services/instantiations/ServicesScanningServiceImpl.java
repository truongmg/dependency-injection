package com.truongmg.di.services.instantiations;

import com.truongmg.di.annotations.*;
import com.truongmg.di.config.configurations.CustomAnnotationConfiguration;
import com.truongmg.di.enums.ScopeType;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ServicesScanningServiceImpl implements ServicesScanningService {

    private final CustomAnnotationConfiguration configuration;

    public ServicesScanningServiceImpl(CustomAnnotationConfiguration configuration) {
        this.configuration = configuration;
        this.init();
    }

    @Override
    public Set<ServiceDetails<?>> mapServices(Set<Class<?>> locatedClasses) {
        final Set<ServiceDetails<?>> serviceDetailsSet = new HashSet<>();
        Set<Class<? extends Annotation>> customServiceAnnotations = configuration.getCustomServiceAnnotations();

        for (Class<?> cls : locatedClasses) {
            if (cls.isInterface()) continue;

            for (Annotation annotation : cls.getAnnotations()) {
                if (customServiceAnnotations.contains(annotation.annotationType())) {
                    ServiceDetails<?> serviceDetails = new ServiceDetails(
                            cls,
                            annotation,
                            this.findSuitableConstructor(cls),
                            this.findVoidMethodWithZeroParamsAndAnnotations(PostConstruct.class, cls),
                            this.findVoidMethodWithZeroParamsAndAnnotations(PreDestroy.class, cls),
                            this.findBeans(cls),
                            this.findScope(cls)
                    );

                    serviceDetailsSet.add(serviceDetails);
                    break;
                }
            }

        }

        return serviceDetailsSet.stream()
                .sorted(new ServiceDetailsConstructComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private ScopeType findScope(Class<?> cls) {
        if (cls.isAnnotationPresent(Scope.class)) {
            return cls.getDeclaredAnnotation(Scope.class).value();
        }
        return ScopeType.DEFAULT_SCOPE;
    }

    private Method[] findBeans(Class<?> cls) {
        final Set<Class<? extends Annotation>> beanAnnotations = this.configuration.getCustomBeanAnnotations();
        final Set<Method> beanMethods = new HashSet<>();

        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0 || method.getReturnType() == void.class || method.getReturnType() == Void.class) {
                continue;
            }

            for (Class<? extends Annotation> beanAnnotation : beanAnnotations) {
                if (method.isAnnotationPresent(beanAnnotation)) {
                    method.setAccessible(true);
                    beanMethods.add(method);
                    break;
                }
            }
        }

        Method[] methods = beanMethods.toArray(new Method[beanMethods.size()]);
        return methods;
    }

    private Constructor<?> findSuitableConstructor(Class<?> cls) {
        for (Constructor<?> ctr : cls.getDeclaredConstructors()) {
            if (ctr.isAnnotationPresent(Autowired.class)) {
                ctr.setAccessible(true);
                return ctr;
            }
        }

        return cls.getConstructors()[0];
    }

    private Method findVoidMethodWithZeroParamsAndAnnotations(Class<? extends Annotation> annotation, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0
                    || method.getReturnType() != void.class
                    && method.getReturnType() != Void.class
                    || !method.isAnnotationPresent(annotation)) {
                continue;
            }

            method.setAccessible(true);
            return method;
        }

        return null;
    }

    private void init() {
        this.configuration.getCustomBeanAnnotations().add(Bean.class);
        this.configuration.getCustomServiceAnnotations().add(Service.class);

    }

}
