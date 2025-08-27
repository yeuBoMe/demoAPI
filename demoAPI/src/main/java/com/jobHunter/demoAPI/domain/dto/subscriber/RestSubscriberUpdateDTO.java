package com.jobHunter.demoAPI.domain.dto.subscriber;

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
public class RestSubscriberUpdateDTO {
    private Long id;
    private String name;
    private String email;
    private Instant updatedAt;
    private String updatedBy;
    private List<String> skills;
}
