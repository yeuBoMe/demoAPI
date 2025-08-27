package com.jobHunter.demoAPI.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "companies")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name must not be blank")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    @Column(unique = true)
    private String name;

    @NotBlank(message = "Description must not be blank")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 255, message = "Logo URL must not exceed 255 characters")
    private String logo;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<User> users;

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Job> jobs;

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
