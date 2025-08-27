package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.pagination.Meta;
import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserViewDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.entity.Company;
import com.jobHunter.demoAPI.domain.entity.Role;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.repository.ResumeRepository;
import com.jobHunter.demoAPI.repository.UserRepository;
import com.jobHunter.demoAPI.service.CompanyService;
import com.jobHunter.demoAPI.service.RoleService;
import com.jobHunter.demoAPI.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final CompanyService companyService;

    private final ResumeRepository resumeRepository;

    private final RoleService roleService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CompanyService companyService,
            ResumeRepository resumeRepository,
            RoleService roleService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.resumeRepository = resumeRepository;
        this.roleService = roleService;
    }

/*@Override
    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageHavUsers = this.userRepository.findAll(spec, pageable);

        // cách truyền thống
*//*        List<RestUserViewDTO> restUserViewDTOList = new ArrayList<>();
        List<User> users = pageHavUsers.getContent();

        for (User user : users) {
            RestUserViewDTO dto = new RestUserViewDTO();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setAge(user.getAge());
            dto.setGender(user.getGender());
            dto.setAddress(user.getAddress());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setUpdateAt(user.getUpdatedAt());
            restUserViewDTOList.add(dto);
        }*//*

     *//*        List<RestUserViewDTO> restUserListDTOS = pageHavUsers.getContent()
                .stream()
                .map(user -> {
                    RestUserViewDTO dto = new RestUserViewDTO();
                    dto.setId(user.getId());
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setAge(user.getAge());
                    dto.setGender(user.getGender());
                    dto.setAddress(user.getAddress());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdateAt(user.getUpdatedAt());
                    return dto;
                })
                .toList();*//*


        // cách chuyên nghiệp hơn
        List<RestUserViewDTO> restUserViewDTOList = pageHavUsers.getContent()
                .stream()
                .map(user -> new RestUserViewDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAge(),
                        user.getGender(),
                        user.getAddress(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavUsers.getTotalPages());
        meta.setTotal(pageHavUsers.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(restUserViewDTOList);

        return resultPaginationDTO;
    }*/

    @Override
    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pageHavUsers = this.userRepository.findAll(spec, pageable);

        List<RestUserViewDTO> restUserViewDTOList = pageHavUsers.getContent()
                .stream()
                .map(this::convertUserToRestUserViewDTO)
                .toList();

        Meta meta = new Meta();
        meta.setCurrent(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageHavUsers.getTotalPages());
        meta.setTotal(pageHavUsers.getTotalElements());

        ResultPaginationDTO resultPaginationDTO = new ResultPaginationDTO();
        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(restUserViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public void handleSaveUserRefreshToken(String refreshToken, String email) {
        User currentUser = this.getUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }

    @Override
    public User getUserById(Long id) {
        return this.userRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("User with id = " + id + " not found!"));
    }

    @Override
    public RestUserViewDTO convertUserToRestUserViewDTO(User user) {
        RestUserViewDTO.CompanyView companyView = Optional.ofNullable(user.getCompany())
                .map(company -> new RestUserViewDTO.CompanyView(company.getId(), company.getName()))
                .orElse(null);

        RestUserViewDTO.RoleView roleView = Optional.ofNullable(user.getRole())
                .map(role -> new RestUserViewDTO.RoleView(role.getId(), role.getName()))
                .orElse(null);

        return new RestUserViewDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getGender(),
                user.getAddress(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                companyView,
                roleView
        );
    }

    @Override
    public User createUser(User user) {
        if (this.checkEmailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email " + user.getEmail() + " already exists!");
        }

        if (user.getCompany() != null) {
            if (!this.companyService.checkIdExists(user.getCompany().getId())) {
                throw new NoSuchElementException("Company with id " + user.getCompany().getId() + " not found!");
            }
            Company companyGetById = this.companyService.getCompanyById(user.getCompany().getId());
            user.setCompany(companyGetById);
        }

        if (user.getRole() != null) {
            if (!this.roleService.checkIdExists(user.getRole().getId())) {
                throw new NoSuchElementException("Role with id " + user.getRole().getId() + " not found!");
            }
            Role roleGetById = this.roleService.getRoleById(user.getRole().getId());
            user.setRole(roleGetById);
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }

    @Override
    public RestUserCreateDTO convertUserToRestUserCreateDTO(User user) {
        RestUserCreateDTO.CompanyCreated companyCreated = Optional.ofNullable(user.getCompany())
                .map(company -> new RestUserCreateDTO.CompanyCreated(company.getId(), company.getName()))
                .orElse(null);

        RestUserCreateDTO restUserCreateDTO = new RestUserCreateDTO();
        restUserCreateDTO.setId(user.getId());
        restUserCreateDTO.setName(user.getName());
        restUserCreateDTO.setEmail(user.getEmail());
        restUserCreateDTO.setAge(user.getAge());
        restUserCreateDTO.setGender(user.getGender());
        restUserCreateDTO.setAddress(user.getAddress());
        restUserCreateDTO.setCreatedAt(user.getCreatedAt());
        restUserCreateDTO.setCompany(companyCreated);
        return restUserCreateDTO;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.userRepository.existsById(id);
    }

    @Override
    public RestUserUpdateDTO convertUserToRestUserUpdateDTO(User user) {
        // cách 1
/*        RestUserUpdateDTO.CompanyUpdated companyUpdated = null;
        if (user.getCompany() != null) {
            companyUpdated = new RestUserUpdateDTO.CompanyUpdated(
                    user.getCompany().getId(),
                    user.getCompany().getName()
            );
        }*/

        // cách chuyên nghiệp
        RestUserUpdateDTO.CompanyUpdated companyUpdated = Optional.ofNullable(user.getCompany())
                .map(company -> new RestUserUpdateDTO.CompanyUpdated(company.getId(), company.getName()))
                .orElse(null);

        return new RestUserUpdateDTO(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getGender(),
                user.getAddress(),
                user.getUpdatedAt(),
                companyUpdated
        );
    }

    @Override
    public User updateUserById(Long id, User userUpdated) {
        User currentUser = this.getUserById(id);

        currentUser.setName(userUpdated.getName());
        currentUser.setAge(userUpdated.getAge());
        currentUser.setGender(userUpdated.getGender());
        currentUser.setAddress(userUpdated.getAddress());

        if (userUpdated.getCompany() != null) {
            if (!this.companyService.checkIdExists(userUpdated.getCompany().getId())) {
                throw new NoSuchElementException("Company not found!");
            }
            Company companyGetById = this.companyService.getCompanyById(userUpdated.getCompany().getId());
            currentUser.setCompany(companyGetById);
        }

        if (userUpdated.getRole() != null) {
            if  (!this.roleService.checkIdExists(userUpdated.getRole().getId())) {
                throw new NoSuchElementException("Role not found!");
            }
            Role roleGetById = this.roleService.getRoleById(userUpdated.getRole().getId());
            currentUser.setRole(roleGetById);
        }

        return this.userRepository.save(currentUser);
    }

//    @Override
//        public User updateUserById(Long id, User userUpdated) throws NoSuchElementException {
//        if (!checkIdExists(id)) {
//            throw new NoSuchElementException("User with id = " + id + " not found!");
//        }
//
//        return getUserById(id)
//                .map(currentUser -> {
//                    currentUser.setName(userUpdated.getName());
//                    currentUser.setAge(userUpdated.getAge());
//                    currentUser.setGender(userUpdated.getGender());
//                    currentUser.setAddress(userUpdated.getAddress());
//                    return this.userRepository.save(currentUser);
//                })
//                .orElseThrow(() -> new NoSuchElementException("User not found"));
//    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("User with id " + id + " not found!");
        }
        this.resumeRepository.deleteByUserId(id);
        this.userRepository.deleteById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }
}
