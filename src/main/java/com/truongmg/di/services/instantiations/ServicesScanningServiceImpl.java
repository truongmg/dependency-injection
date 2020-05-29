package com.truongmg.di.services.instantiations;

import com.truongmg.di.annotations.*;
import com.truongmg.di.config.configurations.ScanningConfiguration;
import com.truongmg.di.enums.ScopeType;
import com.truongmg.di.models.ServiceDetails;
import com.truongmg.di.utils.AliasFinder;
import com.truongmg.di.utils.ServiceDetailsConstructComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ServicesScanningServiceImpl implements ServicesScanningService {

    private final ScanningConfiguration configuration;

    public ServicesScanningServiceImpl(ScanningConfiguration configuration) {
        this.configuration = configuration;
        this.init();
    }

    @Override
    public Set<ServiceDetails> mapServices(Set<Class<?>> locatedClasses) {
        final Map<Class<?>, Annotation> serviceClassesMap = this.filterServiceClasses(locatedClasses);
        final Set<ServiceDetails> serviceDetailsSet = new HashSet<>();

        for (Map.Entry<Class<?>, Annotation> entry : serviceClassesMap.entrySet()) {
            Class<?> cls = entry.getKey();
            Annotation annotation = entry.getValue();

            final ServiceDetails serviceDetails = new ServiceDetails(
                    cls,
                    annotation,
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

    private Map<Class<?>, Annotation> filterServiceClasses(Collection<Class<?>> scannedClasses) {
        final Set<Class<? extends Annotation>> serviceAnnotations = this.configuration.getCustomServiceAnnotations();
        final Map<Class<?>, Annotation> locatedClasses = new HashMap<>();

        for (Class<?> scannedClass : scannedClasses) {
            if (scannedClass.isInterface() || scannedClass.isEnum() || scannedClass.isAnnotation()) {
                continue;
            }

            for (Annotation annotation : scannedClass.getAnnotations()) {
                Class<? extends Annotation> annotationType = annotation.annotationType();
                if (serviceAnnotations.contains(annotationType)) {
                    locatedClasses.put(scannedClass, annotation);
                    break;
                }
            }
        }

        this.configuration.getAdditionalClasses().forEach((cls, annotationCls) -> {
            Annotation annotation = null;
            if (annotationCls != null && cls.isAnnotationPresent(annotationCls)) {
                annotation = cls.getAnnotation(annotationCls);
            }
            locatedClasses.put(cls, annotation);
        });

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
                Class<? extends Annotation> aliasAnnotation = AliasFinder.getAliasAnnotation(declaredAnnotation, Autowired.class);
                if (aliasAnnotation != null) {
                    ctr.setAccessible(true);
                    return ctr;
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
                Class<? extends Annotation> aliasAnnotation = AliasFinder.getAliasAnnotation(declaredAnnotation, annotation);
                if (aliasAnnotation != null) {
                    method.setAccessible(true);
                    return method;
                }
            }

            if (cls.getSuperclass() != null) {
                return this.findVoidMethodWithZeroParamsAndAnnotations(annotation, cls.getSuperclass());
            }
        }

        return null;
    }

    private void init() {
        this.configuration.getCustomBeanAnnotations().add(Bean.class);
        this.configuration.getCustomServiceAnnotations().add(Service.class);

    }

}
