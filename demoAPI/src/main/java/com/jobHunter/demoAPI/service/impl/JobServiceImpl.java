package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.job.RestJobViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.Meta;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Skill;
import com.jobHunter.demoAPI.repository.JobRepository;
import com.jobHunter.demoAPI.repository.ResumeRepository;
import com.jobHunter.demoAPI.service.CompanyService;
import com.jobHunter.demoAPI.service.JobService;
import com.jobHunter.demoAPI.service.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;

    private final CompanyService companyService;

    private final SkillService skillService;

    private final ResumeRepository resumeRepository;

    public JobServiceImpl(
            JobRepository jobRepository,
            CompanyService companyService,
            SkillService skillService,
            ResumeRepository resumeRepository
    ) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.skillService = skillService;
        this.resumeRepository = resumeRepository;
    }

    private void checkNullAndSetSkillsAndCompany(Job job) {
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            for (Skill skill : job.getSkills()) {
                if (!this.skillService.checkIdExists(skill.getId())) {
                    throw new NoSuchElementException("Skill with id " + skill.getId() + " not found!");
                }
            }

            List<Long> skillIds = job.getSkills().stream()
                    .map(Skill::getId)
                    .toList();
            List<Skill> skillList = this.skillService.getSkillsByListId(skillIds);
            job.setSkills(skillList);
        }

        if (job.getCompany() != null) {
            if (!this.companyService.checkIdExists(job.getCompany().getId())) {
                throw new NoSuchElementException("Company with id " + job.getCompany().getId() + " not found!");
            }
            Company companyGetById = this.companyService.getCompanyById(job.getCompany().getId());
            job.setCompany(companyGetById);
        }
    }

    @Override
    public Job createJob(Job job) {
        if (this.checkNameExists(job.getName())) {
            throw new IllegalArgumentException("Job with name " + job.getName() + " already exists!");
        }
        this.checkNullAndSetSkillsAndCompany(job);
        return this.jobRepository.save(job);
    }

    @Override
    public Job updateJobById(Long id, Job jobUpdated) {
        Job jobGetById = this.getJobById(id);

        if (this.checkNameExists(jobUpdated.getName())
                && !jobGetById.getName().equals(jobUpdated.getName())
        ) {
            throw new IllegalArgumentException("Job with name " + jobUpdated.getName() + " already exists!");
        }

        if (jobUpdated.getCompany() != null) {
            if (!this.companyService.checkIdExists(jobUpdated.getCompany().getId())) {
                throw new NoSuchElementException("Company with id " + jobUpdated.getCompany().getId() + " not exists!");
            }
            Company companyGetById = this.companyService.getCompanyById(jobUpdated.getCompany().getId());
            jobGetById.setCompany(companyGetById);
        }

        if (jobUpdated.getSkills() != null && !jobUpdated.getSkills().isEmpty()) {
            for (Skill skill : jobUpdated.getSkills()) {
                if (!this.skillService.checkIdExists(skill.getId())) {
                    throw new NoSuchElementException("Skill with id " + skill.getId() + " not exists!");
                }
            }

            List<Long> idList = jobUpdated.getSkills().stream()
                    .map(Skill::getId)
                    .toList();
            List<Skill> skillList = this.skillService.getSkillsByListId(idList);
            jobGetById.setSkills(skillList);
        }

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
                .orElse(null);

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
                .map(job -> {
                    RestJobViewDTO.CompanyView companyView = Optional.ofNullable(job.getCompany())
                            .map(company -> new RestJobViewDTO.CompanyView(company.getName()))
                            .orElse(null);

                    List<String> skillList = Optional.ofNullable(job.getSkills())
                            .map(skills -> skills.stream()
                                    .map(Skill::getName)
                                    .toList()
                            )
                            .orElse(null);

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
                })
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavJobs.getTotalPages());
        meta.setTotal(pageHavJobs.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(restJobViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public Job getJobById(Long id) {
        return this.jobRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Job with id " + id + " doesn't exist!"));
    }

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

    @Override
    public List<Job> getJobsBySkills(List<Skill> skills) {
        return this.jobRepository.findBySkillsIn(skills);
    }
}
