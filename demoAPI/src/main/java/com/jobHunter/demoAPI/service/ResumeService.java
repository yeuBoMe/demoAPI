package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeCreateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.resume.RestResumeViewDTO;
import com.jobHunter.demoAPI.domain.entity.Resume;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface ResumeService {
    Resume createResume(Resume resume);
    Resume updateResumeById(Long id, Resume resume);
    Resume getResumeById(Long id);

    ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable);
    ResultPaginationDTO fetchResumesByCompany(Specification<Resume> spec, Pageable pageable);
    ResultPaginationDTO fetchResumesByUser(Pageable pageable);

    RestResumeViewDTO convertResumeToRestResumeViewDTO(Resume resume);
    RestResumeCreateDTO convertResumeToRestResumeCreateDTO(Resume resume);
    RestResumeUpdateDTO convertResumeToRestResumeUpdateDTO(Resume resume);

    boolean checkIdExists(Long id);

    void deleteResumeById(Long id);
}
