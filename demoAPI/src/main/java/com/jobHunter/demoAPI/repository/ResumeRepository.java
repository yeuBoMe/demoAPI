package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Resume;
import com.jobHunter.demoAPI.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long>, JpaSpecificationExecutor<Resume> {

    // delete by user id
    void deleteByUserId(Long userId);

    // delete by job id
    void deleteByJobId(Long jobId);
}
