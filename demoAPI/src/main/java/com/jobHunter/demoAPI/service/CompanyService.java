package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.company.RestCompanyViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CompanyService {
    Company createCompany(Company company);
    Company updateCompanyById(Long id, Company company);
    Company getCompanyById(Long id);

    ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable);

    RestCompanyViewDTO convertCompanyToRestCompanyViewDTO(Company company);

    boolean checkNameExists(String name);
    boolean checkIdExists(Long id);

    void deleteCompanyById(Long id);
}
