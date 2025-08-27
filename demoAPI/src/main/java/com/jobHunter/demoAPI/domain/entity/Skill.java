package com.jobHunter.demoAPI.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "skills")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Skill name must not be blank")
    @Size(max = 255, message = "Skill name must not exceed 255 characters")
    @Pattern(
            regexp = "^[\\p{L}\\p{N} .,'\\-]{1,255}$",
            message = "Skill name must contain only letters (with accents), numbers, spaces, dots, commas, apostrophes, or dashes"
    )
    private String name;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Subscriber> subscribers;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }
}
