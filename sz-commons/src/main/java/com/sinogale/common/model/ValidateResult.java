package com.sinogale.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public final class ValidateResult {

    private final String name;

    private final String message;
}