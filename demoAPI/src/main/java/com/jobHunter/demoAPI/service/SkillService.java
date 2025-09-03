package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Skill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface SkillService {
    Skill createSkill(Skill skill);
    Skill updateSkillById(Long id, Skill skill);
    Skill getSkillById(Long id);

    ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable);

    void deleteSkillById(Long id);

    boolean checkIdExists(Long id);
    boolean checkNameExists(String name);
}
