package com.sinogale.codegen.dto;

public @interface GenDto {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
