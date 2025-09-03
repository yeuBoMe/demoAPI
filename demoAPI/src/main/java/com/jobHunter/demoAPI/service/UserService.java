package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserViewDTO;
import com.jobHunter.demoAPI.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    User createUser(User user);
    User updateUserById(Long id, User user);
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByRefreshTokenAndEmail(String refreshToken, String email);

    ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable);

    RestUserCreateDTO convertUserToRestUserCreateDTO(User user);
    RestUserUpdateDTO convertUserToRestUserUpdateDTO(User user);
    RestUserViewDTO convertUserToRestUserViewDTO(User user);

    boolean checkEmailExists(String email);
    boolean checkIdExists(Long id);

    void handleSaveUserRefreshToken(String refreshToken, String email);
    void deleteUserById(Long id);
}


