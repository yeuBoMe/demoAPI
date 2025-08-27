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
public class RestUserUpdateDTO {

    private Long id;
    private String name;
    private int age;
    private GenderEnum gender;
    private String address;

//    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updateAt;

    private CompanyUpdated company;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyUpdated {
        private Long id;
        private String name;
    }
}
