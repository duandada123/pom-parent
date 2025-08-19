package com.sinogale.common.constants;

import java.util.Optional;

public enum BitFlag implements BaseEnum<BitFlag> {
    Y(1, "是"),
    N(0, "否");

    private Integer code;
    private String name;

    private BitFlag(Integer code, String msg) {
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

    public static Optional<BitFlag> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(BitFlag.class, code));
    }
}
