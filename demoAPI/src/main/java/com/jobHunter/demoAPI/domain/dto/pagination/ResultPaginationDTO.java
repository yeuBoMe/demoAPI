package com.jobHunter.demoAPI.domain.dto.pagination;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;
}
