package com.jewelry.pims.common;

import java.util.Collections;
import java.util.List;

public final class PageUtils {

    private PageUtils() {
    }

    public static <T> PageResult<T> paginate(List<T> source, PageQuery query) {
        int pageNo = query == null ? 1 : query.normalizedPageNo();
        int pageSize = query == null ? 10 : query.normalizedPageSize();
        int fromIndex = (pageNo - 1) * pageSize;
        if (source == null || source.isEmpty() || fromIndex >= source.size()) {
            return new PageResult<>(source == null ? 0 : source.size(), Collections.emptyList());
        }
        int toIndex = Math.min(fromIndex + pageSize, source.size());
        return new PageResult<>(source.size(), source.subList(fromIndex, toIndex));
    }
}
