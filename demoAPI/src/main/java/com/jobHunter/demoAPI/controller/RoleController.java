package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleCreateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleViewDTO;
import com.jobHunter.demoAPI.domain.entity.Role;
import com.jobHunter.demoAPI.service.RoleService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @ApiMessage("Create role")
    public ResponseEntity<RestRoleCreateDTO> createRoleRequest(@Valid @RequestBody Role role) {
        Role roleCreated = this.roleService.createRole(role);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.roleService.convertRoleToRestRoleCreateDTO(roleCreated));
    }

    @GetMapping
    @ApiMessage("Fetch all roles")
    public ResponseEntity<ResultPaginationDTO> fetchAllRoleRequest(
            @Filter Specification<Role> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.roleService.fetchAllRoles(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get role")
    public ResponseEntity<RestRoleViewDTO> getRoleByIdRequest(@PathVariable Long id) {
        Role roleGetById = this.roleService.getRoleById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.roleService.convertRoleToRestRoleViewDTO(roleGetById));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update role")
    public ResponseEntity<RestRoleUpdateDTO> updateRoleByIdRequest(
            @PathVariable Long id,
            @Valid @RequestBody Role role
    ) {
        Role roleUpdated = this.roleService.updateRoleById(id, role);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.roleService.convertRoleToRestRoleUpdateDTO(roleUpdated));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Void> deleteRoleByIdRequest(@PathVariable Long id) {
        this.roleService.deleteRoleById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
