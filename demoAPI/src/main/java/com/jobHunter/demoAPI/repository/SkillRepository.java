package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>, JpaSpecificationExecutor<Skill> {

    // exists check
    boolean existsByName(String name);

    // find list skills by list ids
    List<Skill> findAllByIdIn(List<Long> ids);
}
