package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    // check exist
    boolean existsByName(String name);

    // find by list skills
    List<Job> findBySkillsIn(List<Skill> skills);
}
