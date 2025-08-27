package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.job.RestJobViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.service.JobService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @ApiMessage("Create job")
    public ResponseEntity<RestJobViewDTO> createJobRequest(@Valid @RequestBody Job job) {
        Job jobCreated = this.jobService.createJob(job);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.jobService.convertJobToRestJobViewDTO(jobCreated));
    }

    @GetMapping
    @ApiMessage("Fetch all jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJobsRequest(
            @Filter Specification<Job> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.jobService.fetchAllJobs(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get job")
    public ResponseEntity<RestJobViewDTO> getJobRequest(@PathVariable Long id) {
        Job jobGetById = this.jobService.getJobById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.jobService.convertJobToRestJobViewDTO(jobGetById));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update job")
    public ResponseEntity<RestJobViewDTO> updateJobRequest(
            @PathVariable Long id,
            @Valid @RequestBody Job job
    ) {
        Job jobUpdated = this.jobService.updateJobById(id, job);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.jobService.convertJobToRestJobViewDTO(jobUpdated));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Job> createJobRequest(@PathVariable Long id) {
        this.jobService.deleteJobById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
