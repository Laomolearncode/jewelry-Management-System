package com.jewelry.pims.common;

import lombok.Data;

@Data
public class PageQuery {

    private Integer pageNo = 1;
    private Integer pageSize = 10;

    public int normalizedPageNo() {
        return pageNo == null || pageNo < 1 ? 1 : pageNo;
    }

    public int normalizedPageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}
