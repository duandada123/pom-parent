package com.sinogale.common.constants;

import java.util.Optional;

public enum ResourceType implements BaseEnum<ResourceType> {
    MODULE(1, "模块"),
    MENU(2, "菜单"),
    FUNC(3, "功能");

    private Integer code;
    private String name;

    private ResourceType(Integer code, String name) {
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

    public static Optional<ResourceType> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(ResourceType.class, code));
    }
}
