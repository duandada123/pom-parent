package com.sinogale.common.constants;

import java.util.Optional;

public enum SZPlatform implements BaseEnum<SZPlatform> {
    AUTH(1001, "权限系统"),
    OMSSYSTEM(1002, "OMS系统");

    private Integer code;
    private String name;

    private SZPlatform(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static Optional<SZPlatform> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(SZPlatform.class, code));
    }
}
