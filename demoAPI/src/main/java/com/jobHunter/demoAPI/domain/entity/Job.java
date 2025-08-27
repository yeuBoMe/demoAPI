package com.jobHunter.demoAPI.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobHunter.demoAPI.util.constant.LevelEnum;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job name must not be blank")
    @Size(max = 255, message = "Job name must not exceed 255 characters")
    @Pattern(
            regexp = "^[\\p{L}\\p{N} .,'\\-]{1,255}$",
            message = "Job name must contain only letters (with accents), numbers, spaces, dots, commas, apostrophes, or dashes"
    )
    private String name;


    @NotBlank(message = "Location must not be blank")
    @Size(max = 500, message = "Location must not exceed 500 characters")
    @Pattern(regexp = "^[\\p{L}0-9.,'\\-\\/\\s]{1,500}$", message = "Location contains invalid characters")
    private String location;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.0", message = "Salary must be a positive number")
    private double salary;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000, message = "Quantity must not exceed 1000")
    private int quantity;

    @NotNull(message = "Level is required")
    @Enumerated(EnumType.STRING)
    private LevelEnum level;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    private Instant startDate;

    private Instant endDate;

    private boolean active;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"jobs"})
    @JoinTable(
            name = "job_skill",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<Skill> skills;

    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }
}