package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.pagination.Meta;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionCreateDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionViewDTO;
import com.jobHunter.demoAPI.domain.entity.Permission;
import com.jobHunter.demoAPI.repository.PermissionRepository;
import com.jobHunter.demoAPI.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Permission createPermission(Permission permission) {
        if (this.checkNameExists(permission.getName())
                || this.checkApiPathAndModuleAndMethodExists(permission.getApiPath(), permission.getModule(), permission.getMethod())
        ) {
            throw new IllegalArgumentException("Permission already exists!");
        }
        return this.permissionRepository.save(permission);
    }

    @Override
    public ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pageHavPermissions = this.permissionRepository.findAll(spec, pageable);

        List<RestPermissionViewDTO> permissionViewDTOList = pageHavPermissions.getContent()
                .stream()
                .map(permission ->
                        new RestPermissionViewDTO(
                                permission.getId(),
                                permission.getName(),
                                permission.getApiPath(),
                                permission.getMethod(),
                                permission.getModule(),
                                permission.getCreatedAt(),
                                permission.getCreatedBy(),
                                permission.getUpdatedAt(),
                                permission.getUpdatedBy()
                        )
                )
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavPermissions.getTotalPages());
        meta.setTotal(pageHavPermissions.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(permissionViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public Permission getPermissionById(Long id) {
        return this.permissionRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Permission with id " + id + " not found!"));
    }

    @Override
    public Permission updatePermissionById(Long id, Permission permissionUpdated) {
        Permission permissionGetById = this.getPermissionById(id);

        if (this.checkNameExists(permissionUpdated.getName())
                && !permissionGetById.getName().equals(permissionUpdated.getName())
        ) {
            throw new IllegalArgumentException("Permission with name " + permissionUpdated.getName() + " already exists!");
        }

        if (this.checkApiPathAndModuleAndMethodExists(permissionUpdated.getApiPath(), permissionUpdated.getModule(), permissionUpdated.getMethod())
                && !(permissionGetById.getApiPath().equals(permissionUpdated.getApiPath())
                    && permissionGetById.getModule().equals(permissionUpdated.getModule())
                    && permissionGetById.getMethod().equals(permissionUpdated.getMethod()))
        ) {
            throw new IllegalArgumentException(String.format(
                    "Permission with apiPath '%s', module '%s', method '%s' already exists!",
                    permissionUpdated.getApiPath(), permissionUpdated.getModule(), permissionUpdated.getMethod()
            ));
        }

        permissionGetById.setName(permissionUpdated.getName());
        permissionGetById.setApiPath(permissionUpdated.getApiPath());
        permissionGetById.setMethod(permissionUpdated.getMethod());
        permissionGetById.setModule(permissionUpdated.getModule());

        return this.permissionRepository.save(permissionGetById);
    }

    @Override
    public void deletePermissionById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Permission " + id + " not found!");
        }

        Permission permissionGetById = this.getPermissionById(id);
        permissionGetById.getRoles().forEach(role -> role.getPermissions()
                .remove(permissionGetById)
        );

        this.permissionRepository.delete(permissionGetById);
    }

    @Override
    public boolean checkNameExists(String name) {
        return this.permissionRepository.existsByName(name);
    }

    @Override
    public boolean checkApiPathAndModuleAndMethodExists(String apiPath, String module, String method) {
        return this.permissionRepository.existsByApiPathAndModuleAndMethod(apiPath, module, method);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.permissionRepository.existsById(id);
    }

    @Override
    public List<Permission> getPermissionsByListId(List<Long> ids) {
        return this.permissionRepository.findAllByIdIn(ids);
    }

    @Override
    public RestPermissionCreateDTO convertPermissionToRestPermissionCreateDTO(Permission permission) {
        return new RestPermissionCreateDTO(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule(),
                permission.getCreatedAt(),
                permission.getCreatedBy()
        );
    }

    @Override
    public RestPermissionUpdateDTO convertPermissionToRestPermissionUpdateDTO(Permission permission) {
        return new RestPermissionUpdateDTO(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule(),
                permission.getUpdatedAt(),
                permission.getUpdatedBy()
        );
    }

    @Override
    public RestPermissionViewDTO convertPermissionToRestPermissionViewDTO(Permission permission) {
        return new RestPermissionViewDTO(
                permission.getId(),
                permission.getName(),
                permission.getApiPath(),
                permission.getMethod(),
                permission.getModule(),
                permission.getCreatedAt(),
                permission.getCreatedBy(),
                permission.getUpdatedAt(),
                permission.getUpdatedBy()
        );
    }
}
