package com.sinogale.codegen.query;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryItem {
    String desc() default "";
}
