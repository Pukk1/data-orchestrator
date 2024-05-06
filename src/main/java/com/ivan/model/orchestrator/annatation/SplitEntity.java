package com.ivan.model.orchestrator.annatation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SplitEntity {
    Class entity();
    Class entityHandlerRepository();
    Class document();
    Class documentHandlerRepository();
}