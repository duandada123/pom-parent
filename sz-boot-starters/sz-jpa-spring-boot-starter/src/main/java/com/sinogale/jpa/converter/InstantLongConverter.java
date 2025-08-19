package com.sinogale.jpa.converter;

import javax.persistence.AttributeConverter;
import java.time.Instant;

public class InstantLongConverter implements AttributeConverter<Instant, Long> {
    public InstantLongConverter() {
    }

    @Override
    public Long convertToDatabaseColumn(Instant date) {
        return date == null ? null : date.toEpochMilli();
    }

    @Override
    public Instant convertToEntityAttribute(Long date) {
        return date == null ? null : Instant.ofEpochMilli(date);
    }
}
