package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Skill;
import com.jobHunter.demoAPI.service.SkillService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    @ApiMessage("Create skill")
    public ResponseEntity<Skill> createSkillRequest(@Valid @RequestBody Skill skill) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.skillService.createSkill(skill));
    }

    @GetMapping
    @ApiMessage("Fetch all skills")
    public ResponseEntity<ResultPaginationDTO> fetchAllSkillsRequest(
            @Filter Specification<Skill> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.skillService.fetchAllSkills(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get skill")
    public ResponseEntity<Skill> getSkillRequest(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.skillService.getSkillById(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update skill")
    public ResponseEntity<Skill> updateSkillRequest(
            @PathVariable Long id,
            @Valid @RequestBody Skill skill
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.skillService.updateSkillById(id, skill));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkillRequest(@PathVariable Long id) {
        this.skillService.deleteSkillById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
