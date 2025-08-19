package com.sinogale.common.exception;

import com.sinogale.common.constants.ErrorCode;
import com.sinogale.common.model.ValidateResult;

import java.util.List;

public class ValidationException extends BusinessException {
    private List<ValidateResult> result;

    public ValidationException(List<ValidateResult> list) {
        super(ErrorCode.PARAM_SET_ILLEGAL, list);
        this.result = list;
    }

    public List<ValidateResult> getResult() {
        return this.result;
    }
}
