package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.company.RestCompanyViewDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.service.CompanyService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @ApiMessage("Create company")
    public ResponseEntity<Company> createCompanyRequest(@Valid @RequestBody Company company) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.companyService.createCompany(company));
    }

    @GetMapping
    @ApiMessage("Fetch all companies")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompaniesRequest(
            @Filter Specification<Company> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.companyService.fetchAllCompanies(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get company")
    public ResponseEntity<RestCompanyViewDTO> getCompanyRequest(@PathVariable Long id) {
        Company companyGetById = this.companyService.getCompanyById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.companyService.convertCompanyToRestCompanyViewDTO(companyGetById));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update company")
    public ResponseEntity<Company> updateCompanyRequest (
            @PathVariable Long id,
            @Valid @RequestBody Company company
    ) {
        Company companyUpdated = this.companyService.updateCompanyById(id, company);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(companyUpdated);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete company")
    public ResponseEntity<Void> deleteCompanyRequest(@PathVariable Long id) {
        this.companyService.deleteCompanyById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
