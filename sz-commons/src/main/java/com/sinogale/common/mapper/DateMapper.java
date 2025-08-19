package com.sinogale.common.mapper;

import java.time.Instant;
import java.util.Objects;

public class DateMapper {
    public DateMapper() {
    }

    public Long asLong(Instant date) {
        return Objects.nonNull(date) ? date.toEpochMilli() : null;
    }

    public Instant asInstant(Long date) {
        return Objects.nonNull(date) ? Instant.ofEpochMilli(date) : null;
    }
}
