package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleCreateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.role.RestRoleViewDTO;
import com.jobHunter.demoAPI.domain.entity.Permission;
import com.jobHunter.demoAPI.domain.entity.Role;
import com.jobHunter.demoAPI.repository.PermissionRepository;
import com.jobHunter.demoAPI.repository.RoleRepository;
import com.jobHunter.demoAPI.repository.UserRepository;
import com.jobHunter.demoAPI.service.RoleService;
import com.jobHunter.demoAPI.util.pagination.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PermissionRepository permissionRepository
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    private void checkNullAndSetPermissions(Role roleRequest, Role currentRole) {
        if (roleRequest.getPermissions() != null
                && !roleRequest.getPermissions().isEmpty()
        ) {
            for (Permission permission : roleRequest.getPermissions()) {
                if (!this.permissionRepository.existsById(permission.getId())) {
                    throw new NoSuchElementException("Permission with id " + permission.getId() + " not exists");
                }
            }

            List<Long> idList = roleRequest.getPermissions().stream()
                    .map(Permission::getId)
                    .toList();

            List<Permission> permissionList = this.permissionRepository.findAllByIdIn(idList);
            currentRole.setPermissions(permissionList);
        }
    }

    @Override
    public Role createRole(Role role) {
        if (this.checkNameExists(role.getName())) {
            throw new IllegalArgumentException(String.format("Role with name '%s' already exists", role.getName()));
        }
        this.checkNullAndSetPermissions(role, role);
        return this.roleRepository.save(role);
    }

    @Override
    public Role updateRoleById(Long id, Role roleUpdated) {
        Role roleGetById = this.getRoleById(id);

        if (this.checkNameExists(roleUpdated.getName())
                && !roleGetById.getName().equals(roleUpdated.getName())
        ) {
            throw new IllegalArgumentException(String.format("Role with name '%s' already exists", roleUpdated.getName()));
        }

        this.checkNullAndSetPermissions(roleUpdated, roleGetById);

        roleGetById.setName(roleUpdated.getName());
        roleGetById.setDescription(roleUpdated.getDescription());
        roleGetById.setActive(roleUpdated.isActive());

        return this.roleRepository.save(roleGetById);
    }

    @Override
    public void deleteRoleById(Long id) {
        if (!this.roleRepository.existsById(id)) {
            throw new NoSuchElementException("Role with id " + id + " not exists");
        }
        this.userRepository.deleteByRoleId(id);
        this.roleRepository.deleteById(id);
    }

    @Override
    public Role getRoleById(Long id) {
        return this.roleRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Role with id " + id + " not found!"));
    }

    @Override
    public boolean checkNameExists(String name) {
        return this.roleRepository.existsByName(name);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.roleRepository.existsById(id);
    }

    @Override
    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageHavRoles = this.roleRepository.findAll(spec, pageable);

        List<RestRoleViewDTO> restRoleViewDTOList = pageHavRoles.getContent()
                .stream()
                .map(this::convertRoleToRestRoleViewDTO)
                .toList();

        ResultPaginationDTO resultPaginationDTO = PageUtil.handleFetchAllDataWithPagination(pageHavRoles, pageable);
        resultPaginationDTO.setResult(restRoleViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public RestRoleCreateDTO convertRoleToRestRoleCreateDTO(Role role) {
        List<String> permissionList = Optional.ofNullable(role.getPermissions())
                .map(permissions -> permissions.stream()
                        .map(Permission::getName)
                        .toList()
                )
                .orElse(null);

        return new RestRoleCreateDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                role.getCreatedAt(),
                role.getCreatedBy(),
                permissionList
        );
    }

    @Override
    public RestRoleUpdateDTO convertRoleToRestRoleUpdateDTO(Role role) {
        List<String> permissionList = Optional.ofNullable(role.getPermissions())
                .map(permissions -> permissions.stream()
                        .map(Permission::getName)
                        .toList()
                )
                .orElse(null);

        return new RestRoleUpdateDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                role.getUpdatedAt(),
                role.getUpdatedBy(),
                permissionList
        );
    }

    @Override
    public RestRoleViewDTO convertRoleToRestRoleViewDTO(Role role) {
        List<String> permissionList = Optional.ofNullable(role.getPermissions())
                .map(permissions -> permissions.stream()
                        .map(Permission::getName)
                        .toList()
                )
                .orElse(null);

        return new RestRoleViewDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isActive(),
                role.getCreatedAt(),
                role.getCreatedBy(),
                role.getUpdatedAt(),
                role.getUpdatedBy(),
                permissionList
        );
    }
}
