package com.jobHunter.demoAPI.domain.dto.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestEmailJobDTO {

    private String name;
    private double salary;
    private CompanyEmailJob company;
    private List<SkillEmailJob> skills;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyEmailJob {
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillEmailJob {
        private String name;
    }
}
