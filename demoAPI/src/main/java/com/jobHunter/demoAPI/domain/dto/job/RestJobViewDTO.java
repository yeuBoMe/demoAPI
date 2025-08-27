package com.jobHunter.demoAPI.domain.dto.job;

import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.util.constant.LevelEnum;
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
public class RestJobViewDTO {

    private Long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private CompanyView company;
    private List<String> skills;
//    private List<SkillView> skills;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompanyView {
        String name;
    }

/*    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SkillView {
        String name;
    }*/
}
