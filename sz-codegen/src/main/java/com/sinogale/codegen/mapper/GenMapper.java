package com.sinogale.codegen.mapper;


public @interface GenMapper {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
