package com.jobHunter.demoAPI.domain.dto.subscriber;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class RestSubscriberCreateDTO {
    private Long id;
    private String name;
    private String email;
    private Instant createdAt;
    private String createdBy;
    private List<String> skills;
}
