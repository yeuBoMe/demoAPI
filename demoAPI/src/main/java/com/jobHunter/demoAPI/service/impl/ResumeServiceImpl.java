package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.pagination.Meta;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeCreateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeViewDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Resume;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.repository.ResumeRepository;
import com.jobHunter.demoAPI.service.JobService;
import com.jobHunter.demoAPI.service.ResumeService;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;

    private final UserService userService;

    private final JobService jobService;

    private final FilterParser filterParser;

    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            UserService userService,
            JobService jobService,
            FilterParser filterParser,
            FilterSpecificationConverter filterSpecificationConverter
    ) {
        this.resumeRepository = resumeRepository;
        this.userService = userService;
        this.jobService = jobService;
        this.filterParser = filterParser;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @Transactional
    @Override
    public Resume createResume(Resume resume) {
        if (resume.getUser() != null) {
            if (!this.userService.checkIdExists(resume.getUser().getId())) {
                throw new NoSuchElementException("User with id " + resume.getUser().getId() + " not found!");
            }
            User userGetById = this.userService.getUserById(resume.getUser().getId());
            resume.setUser(userGetById);
        }

        if (resume.getJob() != null) {
            if (!this.jobService.checkIdExists(resume.getJob().getId())) {
                throw new NoSuchElementException("Job with id " + resume.getJob().getId() + " not found!");
            }
            Job jobGetById = this.jobService.getJobById(resume.getJob().getId());
            resume.setJob(jobGetById);
        }

        return this.resumeRepository.save(resume);
    }

    @Transactional
    @Override
    public Resume updateResumeById(Long id, Resume resumeUpdated) {
        Resume resumeGetById = this.getResumeById(id);
        resumeGetById.setStatus(resumeUpdated.getStatus());
        return this.resumeRepository.save(resumeGetById);
    }

    @Transactional
    @Override
    public void deleteResumeById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Resume with id " + id + " not found!");
        }
        this.resumeRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageHavResumes = this.resumeRepository.findAll(spec, pageable);

        List<RestResumeViewDTO> restResumeViewDTOList = pageHavResumes.getContent()
                .stream()
                .map(this::convertResumeToRestResumeViewDTO)
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavResumes.getTotalPages());
        meta.setTotal(pageHavResumes.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(restResumeViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public ResultPaginationDTO fetchResumesByCompany(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> pageHavResumes = this.resumeRepository.findAll(spec, pageable);

        List<RestResumeViewDTO> restResumeViewDTOList = pageHavResumes.getContent()
                .stream()
                .map(this::convertResumeToRestResumeViewDTO)
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavResumes.getTotalPages());
        meta.setTotal(pageHavResumes.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(restResumeViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public ResultPaginationDTO fetchResumesByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);

        FilterNode node = this.filterParser.parse("email='" + email + "'");
        FilterSpecification<Resume> spec = this.filterSpecificationConverter.convert(node);
        Page<Resume> pageHavResumes = this.resumeRepository.findAll(spec, pageable);

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavResumes.getTotalPages());
        meta.setTotal(pageHavResumes.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(pageHavResumes.getContent());

        return resultPaginationDTO;
    }

    @Override
    public RestResumeViewDTO convertResumeToRestResumeViewDTO(Resume resume) {
        RestResumeViewDTO.UserView userView = Optional.ofNullable(resume.getUser())
                .map(user -> new RestResumeViewDTO.UserView(user.getId(), user.getName()))
                .orElse(null);

        RestResumeViewDTO.JobView jobView = Optional.ofNullable(resume.getJob())
                .map(job -> new RestResumeViewDTO.JobView(job.getId(), job.getName()))
                .orElse(null);

        String companyName = Optional.ofNullable(resume.getJob())
                .map(Job::getCompany)
                .map(Company::getName)
                .orElse(null);

        return new RestResumeViewDTO(
                resume.getId(),
                resume.getEmail(),
                resume.getUrl(),
                resume.getStatus(),
                resume.getCreatedAt(),
                resume.getUpdatedAt(),
                resume.getCreatedBy(),
                resume.getUpdatedBy(),
                companyName,
                userView,
                jobView
        );
    }

    @Override
    public RestResumeCreateDTO convertResumeToRestResumeCreateDTO(Resume resume) {
        RestResumeCreateDTO.UserResume userResume = Optional.ofNullable(resume.getUser())
                .map(user -> new RestResumeCreateDTO.UserResume(user.getName()))
                .orElse(null);

        RestResumeCreateDTO.JobResume jobResume = Optional.ofNullable(resume.getJob())
                .map(job -> new RestResumeCreateDTO.JobResume(job.getName()))
                .orElse(null);

        return new RestResumeCreateDTO(
                resume.getId(),
                resume.getStatus(),
                resume.getCreatedAt(),
                resume.getCreatedBy(),
                userResume,
                jobResume
        );
    }

    @Override
    public RestResumeUpdateDTO convertResumeToRestResumeUpdateDTO(Resume resume) {
        return new RestResumeUpdateDTO(
                resume.getId(),
                resume.getStatus(),
                resume.getUpdatedAt(),
                resume.getUpdatedBy()
        );
    }

    @Override
    public Resume getResumeById(Long id) {
        return this.resumeRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Resume with id " + id + " not found!"));
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.resumeRepository.existsById(id);
    }
}
