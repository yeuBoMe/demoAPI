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
public class RestSubscriberViewDTO {
    private Long id;
    private String name;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<String> skills;
}
