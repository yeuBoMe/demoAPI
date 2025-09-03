package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Skill;
import com.jobHunter.demoAPI.repository.SkillRepository;
import com.jobHunter.demoAPI.service.SkillService;
import com.jobHunter.demoAPI.util.pagination.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    public SkillServiceImpl(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    @Transactional
    @Override
    public Skill createSkill(Skill skill) {
        if (this.checkNameExists(skill.getName())) {
            throw new IllegalArgumentException("Skill with name " + skill.getName() + " already exists!");
        }
        return this.skillRepository.save(skill);
    }

    @Transactional
    @Override
    public Skill updateSkillById(Long id, Skill skillUpdated) {
        Skill skillGetById = this.getSkillById(id);

        if (this.checkNameExists(skillUpdated.getName())
                && !skillGetById.getName().equals(skillUpdated.getName())
        ) {
            throw new IllegalArgumentException("Skill with name " + skillUpdated.getName() + " already exists!");
        }

        skillGetById.setName(skillUpdated.getName());
        return this.skillRepository.save(skillGetById);
    }

    @Override
    public Skill getSkillById(Long id) {
        return this.skillRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Skill with id " + id + " not exists!"));
    }

    @Override
    public ResultPaginationDTO fetchAllSkills(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageHavSkills = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO resultPaginationDTO = PageUtil.handleFetchAllDataWithPagination(pageHavSkills, pageable);
        resultPaginationDTO.setResult(pageHavSkills.getContent());
        return resultPaginationDTO;
    }

    @Transactional
    @Override
    public void deleteSkillById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Skill with id " + id + " not exists!");
        }

        Skill skillGetById = this.getSkillById(id);
        // delete job inside table job_skill
        skillGetById.getJobs().forEach(job -> job.getSkills()
                .remove(skillGetById)
        );

        // delete subscriber inside table subscriber_skill
        skillGetById.getSubscribers().forEach(subscriber -> subscriber.getSkills()
                .remove(skillGetById)
        );

        // delete skill
        this.skillRepository.delete(skillGetById);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.skillRepository.existsById(id);
    }

    @Override
    public boolean checkNameExists(String name) {
        return this.skillRepository.existsByName(name);
    }

    @Override
    public List<Skill> getSkillsByListId(List<Long> ids) {
        return this.skillRepository.findAllByIdIn(ids);
    }
}
