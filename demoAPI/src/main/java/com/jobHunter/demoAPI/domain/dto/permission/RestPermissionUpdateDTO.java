package com.jobHunter.demoAPI.domain.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RestPermissionUpdateDTO {
    private Long id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private Instant updatedAt;
    private String updatedBy;
}
