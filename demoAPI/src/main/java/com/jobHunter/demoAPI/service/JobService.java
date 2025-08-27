package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.job.RestJobViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Skill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface JobService {

    Job createJob(Job job);

    Job updateJobById(Long id, Job job);

    RestJobViewDTO convertJobToRestJobViewDTO(Job job);

    ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable);

    Job getJobById(Long id);

    void deleteJobById(Long id);

    boolean checkNameExists(String name);

    boolean checkIdExists(Long id);

    List<Job> getJobsBySkills(List<Skill> skills);
}
