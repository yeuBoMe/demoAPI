package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionCreateDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.permission.RestPermissionViewDTO;
import com.jobHunter.demoAPI.domain.entity.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PermissionService {

    Permission createPermission(Permission permission);

    ResultPaginationDTO fetchAllPermissions(Specification<Permission> spec, Pageable pageable);

    Permission getPermissionById(Long id);

    Permission updatePermissionById(Long id, Permission permission);

    void deletePermissionById(Long id);

    boolean checkNameExists(String name);

    boolean checkApiPathAndModuleAndMethodExists(String apiPath, String module, String method);

    boolean checkIdExists(Long id);

    List<Permission> getPermissionsByListId(List<Long> ids);

    RestPermissionCreateDTO convertPermissionToRestPermissionCreateDTO(Permission permission);

    RestPermissionUpdateDTO convertPermissionToRestPermissionUpdateDTO(Permission permission);

    RestPermissionViewDTO convertPermissionToRestPermissionViewDTO(Permission permission);
}
