package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleCreateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleViewDTO;
import com.jobHunter.demoAPI.domain.entity.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {
    Role createRole(Role role);
    Role updateRoleById(Long id, Role role);
    Role getRoleById(Long id);

    void deleteRoleById(Long id);

    boolean checkNameExists(String name);
    boolean checkIdExists(Long id);

    ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable);

    RestRoleCreateDTO convertRoleToRestRoleCreateDTO(Role role);
    RestRoleUpdateDTO convertRoleToRestRoleUpdateDTO(Role role);
    RestRoleViewDTO convertRoleToRestRoleViewDTO(Role role);
}
