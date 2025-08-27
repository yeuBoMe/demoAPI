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
public class RestResumeViewDTO {

    private Long id;
    private String email;
    private String url;
    private StatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private String companyName;
    private UserView user;
    private JobView job;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserView {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class JobView {
        private Long id;
        private String name;
    }
}
