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
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
public class Permission {

    public Permission(
            String name,
            String apiPath,
            String method,
            String module
    ) {
        this.name = name;
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Api path is required")
    private String apiPath;

    @NotBlank(message = "Method is required")
    private String method;

    @NotBlank(message = "Module is required")
    private String module;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Role> roles;

    @PrePersist
    public void beforeCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }
}
