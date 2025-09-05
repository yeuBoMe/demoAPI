package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.company.RestCompanyViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.repository.CompanyRepository;
import com.jobHunter.demoAPI.repository.UserRepository;
import com.jobHunter.demoAPI.service.CompanyService;
import com.jobHunter.demoAPI.util.pagination.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyServiceImpl(
            CompanyRepository companyRepository,
            UserRepository userRepository
    ) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public Company createCompany(Company company) {
        if (checkNameExists(company.getName())) {
            throw new IllegalArgumentException("Company with name '" + company.getName() + "' already exists!");
        }
        return this.companyRepository.save(company);
    }

    @Override
    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageHavCompanies = this.companyRepository.findAll(spec, pageable);

        List<RestCompanyViewDTO> restCompanyViewDTOList = pageHavCompanies.getContent()
                .stream()
                .map(this::convertCompanyToRestCompanyViewDTO)
                .toList();

        ResultPaginationDTO resultPaginationDTO = PageUtil.handleFetchAllDataWithPagination(pageHavCompanies, pageable);
        resultPaginationDTO.setResult(restCompanyViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public Company getCompanyById(Long id) {
        return this.companyRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Company with id " + id + " not found!"));
    }

    @Transactional
    @Override
    public Company updateCompanyById(Long id, Company companyUpdated) {
        Company currentCompany = this.getCompanyById(id);

        if (this.checkNameExists(companyUpdated.getName()) &&
                !currentCompany.getName().equals(companyUpdated.getName())
        ) {
            throw new IllegalArgumentException("Company with name '" + companyUpdated.getName() + "' already exists!");
        }

        currentCompany.setName(companyUpdated.getName());
        currentCompany.setDescription(companyUpdated.getDescription());
        currentCompany.setAddress(companyUpdated.getAddress());
        currentCompany.setLogo(companyUpdated.getLogo());

        return this.companyRepository.save(currentCompany);
    }

    @Override
    public RestCompanyViewDTO convertCompanyToRestCompanyViewDTO(Company company) {
        List<RestCompanyViewDTO.UserInfo> userInfoList = Optional.ofNullable(company.getUsers())
                .map(users -> users.stream()
                        .map(user -> new RestCompanyViewDTO.UserInfo(user.getId(), user.getName()))
                        .toList()
                )
                .orElse(null);

        return new RestCompanyViewDTO(
                company.getId(),
                company.getName(),
                company.getDescription(),
                company.getAddress(),
                company.getLogo(),
                company.getCreatedAt(),
                company.getUpdatedAt(),
                company.getCreatedBy(),
                company.getUpdatedBy(),
                userInfoList
        );
    }

    @Override
    public boolean checkNameExists(String name) {
        return this.companyRepository.existsByName(name);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.companyRepository.existsById(id);
    }

    @Transactional
    @Override
    public void deleteCompanyById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Company with id " + id + " not found!");
        }
        this.userRepository.deleteByCompanyId(id);
        this.companyRepository.deleteById(id);
    }
}
