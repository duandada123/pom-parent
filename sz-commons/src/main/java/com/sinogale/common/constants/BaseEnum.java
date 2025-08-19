package com.sinogale.common.constants;

import java.util.EnumSet;

public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> {

    Integer getCode();

    String getName();

    static <T extends Enum<T> & BaseEnum<T>> T parseByCode(Class<T> cls, Integer code) {
        Enum[] var2 = cls.getEnumConstants();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            T t = (T) var2[var4];
            if (t.getCode().equals(code)) {
                return t;
            }
        }

        return null;
    }

    static <T extends Enum<T> & BaseEnum<V>, V extends Enum<V> & BaseEnum<V>> T parseByName(Class<T> clazz, String name) {
        EnumSet<T> set = EnumSet.allOf(clazz);
        for (T t : set) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    static <T extends Enum<T> & BaseEnum<V>, V extends Enum<V> & BaseEnum<V>> T getByValue(Class<T> clazz, V id) {
        EnumSet<T> set = EnumSet.allOf(clazz);
        for (T t : set) {
            if (t.getCode().equals(id)) {
                return t;
            }
        }
        return null;
    }

    default <T extends Enum> T parseByCode1(final Class<T> cls, final Integer code) {
        for (Enum t : (Enum[]) cls.getEnumConstants()) {
            if (((BaseEnum) t).getCode() == (int) code) {
                return (T) t;
            }
        }
        return null;
    }
}
