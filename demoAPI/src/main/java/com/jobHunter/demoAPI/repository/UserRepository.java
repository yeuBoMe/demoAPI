package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // find user
    User findByEmail(String email);
    User findByRefreshTokenAndEmail(String refreshToken, String email);
    List<User> findByCompany(Company company);

    // exist check
    boolean existsByEmail(String email);

    // delete users
    void deleteByCompanyId(Long companyId);
    void deleteByRoleId(Long roleId);
}
