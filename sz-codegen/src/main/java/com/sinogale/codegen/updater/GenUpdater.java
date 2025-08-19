package com.sinogale.codegen.updater;

public @interface GenUpdater {
    String pkgName();

    String sourcePath() default "src/main/java";

    boolean overrideSource() default false;
}
