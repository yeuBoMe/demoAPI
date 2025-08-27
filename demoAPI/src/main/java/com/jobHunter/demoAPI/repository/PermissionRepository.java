package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, JpaSpecificationExecutor<Permission> {

    // check exists
    boolean existsByName(String name);
    boolean existsByApiPathAndModuleAndMethod(String apiPath, String module, String method);

    // find by list ids
    List<Permission> findAllByIdIn(List<Long> ids);
}
