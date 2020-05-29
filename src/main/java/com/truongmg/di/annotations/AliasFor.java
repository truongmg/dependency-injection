package com.truongmg.di.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface AliasFor {

    Class<? extends Annotation> value();

}
