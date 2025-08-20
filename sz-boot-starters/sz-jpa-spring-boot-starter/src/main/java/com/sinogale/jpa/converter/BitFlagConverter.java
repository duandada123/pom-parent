package com.sinogale.jpa.converter;



import com.sinogale.common.constants.BitFlag;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public class BitFlagConverter implements AttributeConverter<BitFlag, Integer> {

    public BitFlagConverter() {
    }

    @Override
    public Integer convertToDatabaseColumn(BitFlag bt) {
        return bt.getCode();
    }

    @Override
    public BitFlag convertToEntityAttribute(Integer code) {
        return (BitFlag) BitFlag.of(code).orElse(null);
    }
}
