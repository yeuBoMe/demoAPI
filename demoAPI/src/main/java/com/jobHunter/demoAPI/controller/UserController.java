package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserViewDTO;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.jobHunter.demoAPI.util.exception.custom.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsersRequest(
            @Filter Specification<User> spec,
            Pageable pageable
/*            , @RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional*/
    ) {
        /*int current = Integer.parseInt(currentOptional.orElse(""));
        int pageSize = Integer.parseInt(pageSizeOptional.orElse(""));

        Pageable pageable = PageRequest.of(current - 1, pageSize);
        ResultPaginationDTO resultPaginationDTO = this.userService.fetchAllUsers(pageable);*/

        ResultPaginationDTO resultPaginationDTO = this.userService.fetchAllUsers(spec, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(resultPaginationDTO);
    }

    @PostMapping
    @ApiMessage("Create user")
    public ResponseEntity<RestUserCreateDTO> createUserRequest(@Valid @RequestBody User user) {
        User userCreated = this.userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertUserToRestUserCreateDTO(userCreated));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get user")
    public ResponseEntity<RestUserViewDTO> getUserRequest(@PathVariable Long id) {
        User userGetById = this.userService.getUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.userService.convertUserToRestUserViewDTO(userGetById));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update user")
    public ResponseEntity<RestUserUpdateDTO> updateUserRequest(
            @PathVariable Long id,
            @Valid @RequestBody User user
    ) {
        User userUpdated = this.userService.updateUserById(id, user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.userService.convertUserToRestUserUpdateDTO(userUpdated));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete user")
    public ResponseEntity<Void> deleteUserRequest(@PathVariable Long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("Id invalid");
        }

        this.userService.deleteUserById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
