package com.sinogale.dksdkdemo.enums;

/**
 * DataWorks 异步操作状态。
 *
 * <p>以 ACCEPTED 结尾的状态只表示上游已受理请求，不代表操作已经完成。</p>
 */
public enum DataWorksOperationState {
    SUBMIT_ACCEPTED,
    PUBLISH_ACCEPTED,
    RUN_ACCEPTED,
    WAITING,
    SUBMIT_FAILED,
    CHECK_FAILED,
    STATUS_ONLY;

    public boolean isAccepted() {
        return this == SUBMIT_ACCEPTED || this == PUBLISH_ACCEPTED || this == RUN_ACCEPTED;
    }
}
