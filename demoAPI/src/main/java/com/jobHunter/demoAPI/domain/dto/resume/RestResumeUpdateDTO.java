package com.jobHunter.demoAPI.domain.dto.resume;

import com.jobHunter.demoAPI.util.constant.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestResumeUpdateDTO {
    private Long id;
    private StatusEnum status;
    private Instant updatedAt;
    private String updatedBy;
}
