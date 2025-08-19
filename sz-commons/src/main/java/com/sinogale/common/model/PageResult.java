package com.sinogale.common.model;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;

    private Integer totalPages;

    int pageSize;

    int pageNumber;

    private List<T> list;



    public PageResult(List<T> list, Long total, Integer pageSize, Integer pageNumber) {
        this.list = list;
        this.total = total.longValue();
        this.pageSize = pageSize.intValue();
        this.pageNumber = pageNumber.intValue();
    }

    public static <T> PageResult of(List<T> list, Long total, Integer pageSize, Integer pageNumber) {
        return new PageResult(list, total, pageSize, pageNumber);
    }

    public PageResult() {
    }
}
