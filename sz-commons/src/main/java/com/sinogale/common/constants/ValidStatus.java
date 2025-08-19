package com.sinogale.common.constants;

import java.util.Optional;

public enum ValidStatus implements BaseEnum<ValidStatus> {
    VALID(1, "valid"),
    INVALID(0, "invalid");

    private Integer code;
    private String name;

    private ValidStatus(Integer code, String msg) {
        this.code = code;
        this.name = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public static Optional<ValidStatus> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(ValidStatus.class, code));
    }
}
