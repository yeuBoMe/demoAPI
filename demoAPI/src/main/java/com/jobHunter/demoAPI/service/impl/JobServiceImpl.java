package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.job.RestJobViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Skill;
import com.jobHunter.demoAPI.repository.JobRepository;
import com.jobHunter.demoAPI.repository.ResumeRepository;
import com.jobHunter.demoAPI.repository.SkillRepository;
import com.jobHunter.demoAPI.service.CompanyService;
import com.jobHunter.demoAPI.service.JobService;
import com.jobHunter.demoAPI.util.pagination.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final CompanyService companyService;

    private final SkillRepository skillRepository;

    private final ResumeRepository resumeRepository;

    public JobServiceImpl(
            JobRepository jobRepository,
            CompanyService companyService,
            SkillRepository skillRepository,
            ResumeRepository resumeRepository
    ) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.skillRepository = skillRepository;
        this.resumeRepository = resumeRepository;
    }

    private void checkNullAndSetSkillsAndCompany(Job jobRequest, Job currentJob) {
        if (jobRequest.getSkills() != null && !jobRequest.getSkills().isEmpty()) {
            for (Skill skill : jobRequest.getSkills()) {
                if (!this.skillRepository.existsById(skill.getId())) {
                    throw new NoSuchElementException("Skill with id " + skill.getId() + " not found!");
                }
            }

            List<Long> skillIds = jobRequest.getSkills().stream()
                    .map(Skill::getId)
                    .toList();

            List<Skill> skillList = this.skillRepository.findAllByIdIn(skillIds);
            currentJob.setSkills(skillList);
        }

        if (jobRequest.getCompany() != null) {
            if (!this.companyService.checkIdExists(jobRequest.getCompany().getId())) {
                throw new NoSuchElementException("Company with id " + jobRequest.getCompany().getId() + " not found!");
            }
            Company companyGetById = this.companyService.getCompanyById(jobRequest.getCompany().getId());
            currentJob.setCompany(companyGetById);
        }
    }

    @Transactional
    @Override
    public Job createJob(Job job) {
        if (this.checkNameExists(job.getName())) {
            throw new IllegalArgumentException("Job with name '" + job.getName() + "' already exists!");
        }
        this.checkNullAndSetSkillsAndCompany(job, job);
        return this.jobRepository.save(job);
    }

    @Transactional
    @Override
    public Job updateJobById(Long id, Job jobUpdated) {
        Job jobGetById = this.getJobById(id);

        if (this.checkNameExists(jobUpdated.getName())
                && !jobGetById.getName().equals(jobUpdated.getName())
        ) {
            throw new IllegalArgumentException("Job with name '" + jobUpdated.getName() + "' already exists!");
        }

        this.checkNullAndSetSkillsAndCompany(jobUpdated, jobGetById);

        jobGetById.setName(jobUpdated.getName());
        jobGetById.setLocation(jobUpdated.getLocation());
        jobGetById.setSalary(jobUpdated.getSalary());
        jobGetById.setQuantity(jobUpdated.getQuantity());
        jobGetById.setLevel(jobUpdated.getLevel());
        jobGetById.setDescription(jobUpdated.getDescription());
        jobGetById.setActive(jobUpdated.isActive());

        return this.jobRepository.save(jobGetById);
    }

    @Override
    public RestJobViewDTO convertJobToRestJobViewDTO(Job job) {
        RestJobViewDTO.CompanyView companyView = Optional.ofNullable(job.getCompany())
                .map(company -> new RestJobViewDTO.CompanyView(company.getName()))
                .orElse(null);

        List<String> skillList = Optional.ofNullable(job.getSkills())
                .map(skills -> skills.stream()
                        .map(Skill::getName)
                        .toList()
                )
                .orElse(new ArrayList<>());

        return new RestJobViewDTO(
                job.getId(),
                job.getName(),
                job.getLocation(),
                job.getSalary(),
                job.getQuantity(),
                job.getLevel(),
                job.getDescription(),
                job.getStartDate(),
                job.getEndDate(),
                job.isActive(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getCreatedBy(),
                job.getUpdatedBy(),
                companyView,
                skillList
        );
    }

    @Override
    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageHavJobs = this.jobRepository.findAll(spec, pageable);

        List<RestJobViewDTO> restJobViewDTOList = pageHavJobs.getContent()
                .stream()
                .map(this::convertJobToRestJobViewDTO)
                .toList();

        ResultPaginationDTO resultPaginationDTO = PageUtil.handleFetchAllDataWithPagination(pageHavJobs, pageable);
        resultPaginationDTO.setResult(restJobViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public Job getJobById(Long id) {
        return this.jobRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job with id " + id + " doesn't exist!"));
    }

    @Transactional
    @Override
    public void deleteJobById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Job with id " + id + " not found!");
        }
        this.resumeRepository.deleteByJobId(id);
        this.jobRepository.deleteById(id);
    }

    @Override
    public boolean checkNameExists(String name) {
        return this.jobRepository.existsByName(name);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.jobRepository.existsById(id);
    }
}
