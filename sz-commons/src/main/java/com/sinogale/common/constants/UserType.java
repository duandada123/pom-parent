package com.sinogale.common.constants;

import java.util.Optional;

public enum UserType implements BaseEnum<UserType> {
    ADMIN(1, "管理员"),
    USER(2, "用户");

    private Integer code;
    private String name;

    private UserType(Integer code, String name) {
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

    public static Optional<UserType> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(UserType.class, code));
    }
}
