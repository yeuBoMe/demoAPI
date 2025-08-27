package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserViewDTO;
import com.jobHunter.demoAPI.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable);

    User getUserById(Long id);

    User createUser(User user);

    RestUserCreateDTO convertUserToRestUserCreateDTO(User user);

    RestUserUpdateDTO convertUserToRestUserUpdateDTO(User user);

    RestUserViewDTO convertUserToRestUserViewDTO(User user);

    void handleSaveUserRefreshToken(String refreshToken, String email);

    User updateUserById(Long id, User user);

    User getUserByEmail(String email);

    User getUserByRefreshTokenAndEmail(String refreshToken, String email);

    boolean checkEmailExists(String email);

    boolean checkIdExists(Long id);

    void deleteUserById(Long id);
}


