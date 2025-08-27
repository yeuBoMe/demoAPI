package com.jobHunter.demoAPI.domain.entity;

import com.jobHunter.demoAPI.util.constant.StatusEnum;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Column(unique = true)
    @Pattern(
            regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$",
            message = "Email format is invalid"
    )
    private String email;

    @NotBlank(message = "URL must not be blank")
    private String url;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

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
