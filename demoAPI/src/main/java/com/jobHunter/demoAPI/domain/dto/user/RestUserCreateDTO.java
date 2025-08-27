package com.jobHunter.demoAPI.domain.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jobHunter.demoAPI.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestUserCreateDTO {

    private Long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;

//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    private CompanyCreated company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyCreated {
        private Long id;
        private String name;
    }
}
