package com.sinogale.common.model;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class PageRequestWrapper<T> {

    public static final int MAX_COUNT = 100;

    private T bean;

    private Integer pageSize = 10;

    private Integer page = 1;

    private Map<String, String> sorts;

    public Integer getPageSize() {
        if (Objects.isNull(this.pageSize)) {
            return 10;
        } else {
            return this.pageSize <= 100 && this.pageSize >= 0 ? this.pageSize : 10;
        }
    }




}
