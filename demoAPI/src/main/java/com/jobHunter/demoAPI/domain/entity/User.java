package com.jobHunter.demoAPI.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobHunter.demoAPI.util.constant.GenderEnum;
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
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

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

    @Size(min = 6, message = "Password must be at least 6 characters long!")
    private String password;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be a positive number")
    @Max(value = 150, message = "Age must be realistic")
    private int age;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @NotBlank(message = "Address must not be blank")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    //    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;

    //    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;

    private String createdBy;

    private String updatedBy;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Resume> resumes;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.createdBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
        this.updatedBy = SecurityUtil.getCurrentUserLogin().orElse("");
    }
}
