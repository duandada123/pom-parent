package com.sinogale.jpa.converter;


import com.sinogale.common.constants.ValidStatus;

import javax.persistence.AttributeConverter;

public class ValidStatusConverter implements AttributeConverter<ValidStatus, Integer> {
    public ValidStatusConverter() {
    }

    @Override
    public Integer convertToDatabaseColumn(ValidStatus validStatus) {
        return validStatus.getCode();
    }

    @Override
    public ValidStatus convertToEntityAttribute(Integer code) {
        return (ValidStatus)ValidStatus.of(code).orElse(null);
    }
}
