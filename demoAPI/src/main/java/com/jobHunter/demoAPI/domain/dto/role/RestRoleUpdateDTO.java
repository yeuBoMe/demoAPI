package com.jobHunter.demoAPI.domain.dto.role;

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
public class RestRoleUpdateDTO {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Instant updatedAt;
    private String updatedBy;
    List<String> permissions;
}
