package com.jobHunter.demoAPI.util.pagination;

import com.jobHunter.demoAPI.domain.dto.pagination.Meta;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class PageUtil {

    public static <T> ResultPaginationDTO handleFetchAllDataWithPagination(Page<T> pageHavData, Pageable pageable) {
        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavData.getTotalPages());
        meta.setTotal(pageHavData.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        return resultPaginationDTO;
    }
}
