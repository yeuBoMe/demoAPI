package com.jobHunter.demoAPI.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.persistence.*;
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

@Entity
@Table(name = "subscribers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    @Pattern(
            regexp = "^[A-Za-zÀ-ỹà-ỹ'\\-\\.\\s]{1,100}$",
            message = "Name must only contain letters, spaces, apostrophes, dashes or dots"
    )
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    @Column(unique = true)
    @Pattern(
            regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$",
            message = "Email format is invalid"
    )
    private String email;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"subscribers"})
    @JoinTable(
            name = "subscriber_skill",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    public List<Skill> skills;

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
