package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {

    // exist check
    boolean existsByName(String name);
}
