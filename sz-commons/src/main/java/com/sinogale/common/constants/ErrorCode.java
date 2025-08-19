package com.sinogale.common.constants;

import java.util.Optional;

public enum ErrorCode implements BaseEnum<ErrorCode> {
    SUCCESS(1, "操作成功"),
         FAIL(0, "操作失败"),
    NOT_FIND_ERROR(20000001, "未查询到信息"),
    SAVE_ERROR(20000002, "保存信息失败"),
    UPDATE_ERROR(20000003, "更新信息失败"),
    VALIDATE_ERROR(20000004, "数据检验失败"),
    TOKEN_EXPIRED(20000005, "令牌过期"),
    STATUS_HAS_VALID(2000006, "状态已经被启用"),
    STATUS_HAS_INVALID(2000007, "状态已经被禁用"),
    SYSTEM_ERROR(10000001, "系统异常"),
    BUSINESS_ERROR(10000002, "业务异常"),
    PARAM_SET_ILLEGAL(10000003, "参数设置非法"),
    TRANSFER_STATUS_ERROR(10000004, "当前状态不正确，请勿重复提交"),
    ACCESS_DENIED(10000005, "没有访问该资源的权限，请联系管理员");

    private Integer code;
    private String msg;

    private ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.msg;
    }

    public static Optional<ErrorCode> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(ErrorCode.class, code));
    }
}
