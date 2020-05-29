package com.truongmg.di.services.instantiations;

import com.truongmg.di.annotations.*;
import com.truongmg.di.config.configurations.CustomAnnotationConfiguration;
import com.truongmg.di.enums.ScopeType;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ServicesScanningServiceImpl implements ServicesScanningService {

    private final CustomAnnotationConfiguration configuration;

    public ServicesScanningServiceImpl(CustomAnnotationConfiguration configuration) {
        this.configuration = configuration;
        this.init();
    }

    @Override
    public Set<ServiceDetails> mapServices(Set<Class<?>> locatedClasses) {
        final Map<Class<?>, List<Class<? extends Annotation>>> serviceClassesMap = this.filterServiceClasses(locatedClasses);
        final Set<ServiceDetails> serviceDetailsSet = new HashSet<>();

        for (Map.Entry<Class<?>, List<Class<? extends Annotation>>> entry : serviceClassesMap.entrySet()) {
            Class<?> cls = entry.getKey();
            List<Class<? extends Annotation>> annotations = entry.getValue();

            final ServiceDetails serviceDetails = new ServiceDetails(
                    cls,
                    annotations,
                    this.findSuitableConstructor(cls),
                    this.findVoidMethodWithZeroParamsAndAnnotations(PostConstruct.class, cls),
                    this.findVoidMethodWithZeroParamsAndAnnotations(PreDestroy.class, cls),
                    this.findBeans(cls),
                    this.findScope(cls)
            );

            serviceDetailsSet.add(serviceDetails);
        }

        return serviceDetailsSet.stream()
                .sorted(new ServiceDetailsConstructComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Map<Class<?>, List<Class<? extends Annotation>>> filterServiceClasses(Collection<Class<?>> scannedClasses) {
        final Set<Class<? extends Annotation>> serviceAnnotations = this.configuration.getCustomServiceAnnotations();
        final Map<Class<?>, List<Class<? extends Annotation>>> locatedClasses = new HashMap<>();

        for (Class<?> scannedClass : scannedClasses) {
            if (scannedClass.isInterface() || scannedClass.isEnum() || scannedClass.isAnnotation()) {
                continue;
            }

            for (Annotation annotation : scannedClass.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (serviceAnnotations.contains(annotationType)) {
                    locatedClasses.put(scannedClass, Collections.singletonList(annotationType));
                    break;
                }

                if (annotationType.isAnnotationPresent(AliasFor.class)) {
                    Class<? extends Annotation> aliasValue = annotationType.getAnnotation(AliasFor.class).value();
                    if (serviceAnnotations.contains(aliasValue)) {
                        locatedClasses.put(scannedClass, Arrays.asList(new Class[]{scannedClass, annotationType}));
                        break;
                    }
                }
            }

        }

        return locatedClasses;
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

                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    Class<? extends Annotation> annotationType = declaredAnnotation.annotationType();
                    if (annotationType.isAnnotationPresent(AliasFor.class)) {
                        final Class<? extends Annotation> aliasValue = annotationType.getAnnotation(AliasFor.class).value();

                        if (aliasValue == beanAnnotation) {
                            method.setAccessible(true);
                            beanMethods.add(method);

                            break;
                        }
                    }
                }

            }
        }

        return beanMethods.toArray(new Method[beanMethods.size()]);
    }

    private Constructor<?> findSuitableConstructor(Class<?> cls) {
        for (Constructor<?> ctr : cls.getDeclaredConstructors()) {
            if (ctr.isAnnotationPresent(Autowired.class)) {
                ctr.setAccessible(true);
                return ctr;
            }

            for (Annotation declaredAnnotation : ctr.getDeclaredAnnotations()) {
                Class<? extends Annotation> annotationType = declaredAnnotation.annotationType();
                if (annotationType.isAnnotationPresent(AliasFor.class)) {
                    Class<? extends Annotation> aliasValue = annotationType.getAnnotation(AliasFor.class).value();
                    if (aliasValue == Autowired.class) {
                        ctr.setAccessible(true);
                        return ctr;
                    }
                }
            }
        }



        return cls.getConstructors()[0];
    }

    private Method findVoidMethodWithZeroParamsAndAnnotations(Class<? extends Annotation> annotation, Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getParameterCount() != 0
                    || (method.getReturnType() != void.class && method.getReturnType() != Void.class)) {
                continue;
            }

            if (method.isAnnotationPresent(annotation)) {
                method.setAccessible(true);
                return method;
            }

            for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                Class<? extends Annotation> annotationType = declaredAnnotation.annotationType();
                if (annotationType.isAnnotationPresent(AliasFor.class)) {
                    final Class<? extends Annotation> aliasValue = annotationType.getAnnotation(AliasFor.class).value();
                    if (aliasValue == Autowired.class) {
                        method.setAccessible(true);
                        return method;
                    }
                }
            }

            return null;
        }

        return null;
    }

    private void init() {
        this.configuration.getCustomBeanAnnotations().add(Bean.class);
        this.configuration.getCustomServiceAnnotations().add(Service.class);

    }

}
