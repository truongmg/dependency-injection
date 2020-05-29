package com.truongmg.di.utils;

import com.truongmg.di.annotations.AliasFor;

import java.lang.annotation.Annotation;

public class AliasFinder {

    public static Class<? extends Annotation> getAliasAnnotation(Annotation declaredAnnotation, Class<? extends Annotation> requiredAnnotation) {
        Class<? extends Annotation> annotationType = declaredAnnotation.annotationType();
        if (annotationType.isAnnotationPresent(AliasFor.class)) {
            Class<? extends Annotation> aliasValue = annotationType.getAnnotation(AliasFor.class).value();
            if (aliasValue == requiredAnnotation) {
                return aliasValue;
            }
        }
        return null;
    }

}
