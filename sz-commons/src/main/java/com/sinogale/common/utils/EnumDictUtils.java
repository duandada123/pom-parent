package com.sinogale.common.utils;

import com.sinogale.common.constants.BaseEnum;
import com.sinogale.common.model.EnumDict;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class EnumDictUtils {
    private EnumDictUtils() {
    }

    public static <T extends Enum<T> & BaseEnum<T>> List<EnumDict> getEnumDict(Class<T> cls) {
        return (List) EnumSet.allOf(cls).stream().map((et) -> {
            return new EnumDict(((BaseEnum)et).getName(), ((BaseEnum)et).getCode(), et.name());
        }).collect(Collectors.toList());
    }
}
