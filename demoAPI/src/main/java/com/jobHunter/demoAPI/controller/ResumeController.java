package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeCreateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeViewDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Resume;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.service.ResumeService;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    private final UserService userService;

    private final FilterBuilder filterBuilder;

    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(
            ResumeService resumeService,
            UserService userService,
            FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter
    ) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping
    @ApiMessage("Create resume")
    public ResponseEntity<RestResumeCreateDTO> createResumeRequest(@Valid @RequestBody Resume resume) {
        Resume resumeCreated = this.resumeService.createResume(resume);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.resumeService.convertResumeToRestResumeCreateDTO(resumeCreated));
    }

    @GetMapping
    @ApiMessage("Fetch all resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResumesRequest(
            @Filter Specification<Resume> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.resumeService.fetchAllResumes(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get resume")
    public ResponseEntity<RestResumeViewDTO> getResumeRequest(@PathVariable Long id) {
        Resume resumeGetById = this.resumeService.getResumeById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.resumeService.convertResumeToRestResumeViewDTO(resumeGetById));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update resume")
    public ResponseEntity<RestResumeUpdateDTO> updateResumeRequest(
            @PathVariable Long id,
            @Valid @RequestBody Resume resume
    ) {
        Resume resumeUpdated = this.resumeService.updateResumeById(id, resume);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.resumeService.convertResumeToRestResumeUpdateDTO(resumeUpdated));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> deleteResumeRequest(@PathVariable Long id) {
        this.resumeService.deleteResumeById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @GetMapping("/by-company")
    @ApiMessage("Get resumes by company")
    public ResponseEntity<ResultPaginationDTO> getResumesByCompanyRequest(
            @Filter Specification<Resume> spec,
            Pageable pageable
    ) {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(() ->
                new IllegalStateException("User not logged in")
        );

        User userGetByEmail = this.userService.getUserByEmail(email);
        if (userGetByEmail == null) {
            throw new NoSuchElementException(String.format("User with email %s not found", email));
        }

        Company company = userGetByEmail.getCompany();
        if (company == null) {
            throw new NoSuchElementException("Company not found");
        }

        List<Long> arrJobIds = null;
        List<Job> jobs = company.getJobs();
        if (jobs != null && !jobs.isEmpty()) {
            arrJobIds = jobs.stream()
                    .map(Job::getId)
                    .toList();
        }

        Specification<Resume> jobInSpec = this.filterSpecificationConverter.convert(
                this.filterBuilder.field("job")
                        .in(this.filterBuilder.input(Objects.requireNonNull(arrJobIds)))
                        .get()
        );
        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.resumeService.fetchResumesByCompany(finalSpec, pageable));
    }

    @PostMapping("/by-user")
    @ApiMessage("Get resumes by user")
    public ResponseEntity<ResultPaginationDTO> getResumesByUserRequest(Pageable pageable) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.resumeService.fetchResumesByUser(pageable));
    }
}
