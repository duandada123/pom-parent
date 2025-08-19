package com.sinogale.codegen.query;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenQuery {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
