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
public class RestResumeCreateDTO {

    private Long id;
    private StatusEnum status;
    private Instant createdAt;
    private String createdBy;

    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserResume {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobResume {
        private String name;
    }
}
