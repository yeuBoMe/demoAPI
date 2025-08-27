package com.jobHunter.demoAPI.domain.dto.company;

import com.jobHunter.demoAPI.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestCompanyViewDTO {

    private Long id;
    private String name;
    private String description;
    private String address;
    private String logo;

    //    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    //    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private List<UserInfo> users;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private String name;
    }
}
