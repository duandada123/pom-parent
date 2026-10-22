package com.sinogale.dwsdk;

import com.sinogale.dwsdk.dto.request.*;
import com.sinogale.dwsdk.dto.response.*;
import com.sinogale.dwsdk.exception.DataWorksException;
import com.sinogale.dwsdk.service.DataWorksLogService;
import com.sinogale.dwsdk.service.DataWorksService;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DataWorks 强类型调用入口。直接注入即可使用，不依赖 Controller 或组件扫描。
 * 在入口校验参数，使 Java 调用与 HTTP 调用具有相同的校验约束。
 */
public class DwSdkClient {
    private final DataWorksService files;
    private final DataWorksLogService logs;
    private final Validator validator;

    public DwSdkClient(DataWorksService files, DataWorksLogService logs, Validator validator) {
        this.files = files;
        this.logs = logs;
        this.validator = validator;
    }

    public CreateScriptResponse create(CreateScriptRequest request) {
        validate(request);
        return files.create(request);
    }

    public SubmitScriptResponse submit(SubmitScriptRequest request) {
        validate(request);
        return files.submit(request);
    }

    public DeployScriptResponse deploy(DeployScriptRequest request) {
        validate(request);
        return files.deploy(request);
    }

    public QueryLogsResponse queryLogs(QueryLogsRequest request) {
        validate(request);
        return logs.query(request);
    }

    private <T> void validate(T request) {
        if (request == null) {
            throw new DataWorksException(400, "INVALID_REQUEST", "请求对象不能为空", null);
        }
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .sorted().collect(Collectors.joining("; "));
            throw new DataWorksException(400, "INVALID_REQUEST", message, null);
        }
    }
}
