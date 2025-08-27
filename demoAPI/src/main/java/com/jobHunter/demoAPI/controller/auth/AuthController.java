package com.jobHunter.demoAPI.controller.auth;

import com.jobHunter.demoAPI.domain.dto.auth.LoginDTO;
import com.jobHunter.demoAPI.domain.dto.auth.RestLoginDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.entity.Permission;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.jobHunter.demoAPI.util.exception.custom.IdInvalidException;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final UserService userService;

    private final SecurityUtil securityUtil;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(
            UserService userService,
            SecurityUtil securityUtil,
            AuthenticationManagerBuilder authenticationManagerBuilder
    ) {
        this.userService = userService;
        this.securityUtil = securityUtil;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/login")
    @ApiMessage("Login successfully")
    public ResponseEntity<RestLoginDTO> loginRequest(@Valid @RequestBody LoginDTO loginDTO) {
        /* Nạp input gồm username & password vào spring security bằng class chứa thông tin username & pass chưa xác thực */
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                );

        /* Xác thực người dùng bằng cách gửi UsernamePasswordAuthenticationToken vào Spring Security,
           nếu hợp lệ => gọi hàm loadUserByUsername và tạo object Authentication (đã xác thực) */
        Authentication authentication = this.authenticationManagerBuilder
                .getObject()
                .authenticate(authenticationToken);

        // lưu data vào spring security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        RestLoginDTO restLoginDTO = new RestLoginDTO();
        User currentUserInDB = this.userService.getUserByEmail(loginDTO.getUsername());

        if (currentUserInDB != null) {
/*            if (currentUserInDB.getRole() == null) {
                throw new IllegalStateException("User role is not assigned!");
            }*/
            if (currentUserInDB.getRole() != null
                    && currentUserInDB.getRole().getPermissions() != null
                    && !currentUserInDB.getRole().getPermissions().isEmpty()
            ) {
                List<String> permissions = currentUserInDB.getRole().getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toList();

                RestLoginDTO.UserLogin.RoleUserLogin roleUserLogin = new RestLoginDTO.UserLogin.RoleUserLogin(
                        currentUserInDB.getRole().getName(),
                        permissions
                );

                RestLoginDTO.UserLogin userLoginWithRole = new RestLoginDTO.UserLogin(
                        currentUserInDB.getId(),
                        currentUserInDB.getName(),
                        currentUserInDB.getEmail(),
                        roleUserLogin
                );
                restLoginDTO.setUser(userLoginWithRole);
            } else {
                restLoginDTO.setUser(new RestLoginDTO.UserLogin(
                        currentUserInDB.getId(),
                        currentUserInDB.getName(),
                        currentUserInDB.getEmail(),
                        null
                ));
            }
        }

        // Create access & refresh token
        String accessToken = this.securityUtil.createAccessToken(authentication.getName(), restLoginDTO);
        restLoginDTO.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), restLoginDTO);
        this.userService.handleSaveUserRefreshToken(refreshToken, loginDTO.getUsername());

        // set cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true) // server can read
                .secure(true) // use with https (false: use with http)
                .path("/") // use with all api links in project
                .maxAge(this.refreshTokenExpiration)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(restLoginDTO);
    }

    @PostMapping("/register")
    @ApiMessage("Register successfully")
    public ResponseEntity<RestUserCreateDTO> registerRequest(@Valid @RequestBody User user) {
        User userCreated = this.userService.createUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertUserToRestUserCreateDTO(userCreated));
    }

    @GetMapping("/account")
    @ApiMessage("Get user information")
    public ResponseEntity<RestLoginDTO.UserGetAccount> getAccountRequest() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User currentUser = this.userService.getUserByEmail(email);

        RestLoginDTO.UserGetAccount userGetAccount = new RestLoginDTO.UserGetAccount();

        if (currentUser != null) {
            if (currentUser.getRole() != null
                    && currentUser.getRole().getPermissions() != null
                    && !currentUser.getRole().getPermissions().isEmpty()
            ) {
                List<String> permissions = currentUser.getRole().getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .toList();

                RestLoginDTO.UserLogin.RoleUserLogin roleUserLogin = new RestLoginDTO.UserLogin.RoleUserLogin(
                        currentUser.getRole().getName(),
                        permissions
                );

                userGetAccount.setUser(new RestLoginDTO.UserLogin(
                        currentUser.getId(),
                        currentUser.getName(),
                        currentUser.getEmail(),
                        roleUserLogin
                ));
            } else {
                userGetAccount.setUser(new RestLoginDTO.UserLogin(
                        currentUser.getId(),
                        currentUser.getName(),
                        currentUser.getEmail(),
                        null
                ));
            }
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGetAccount);
    }

    @GetMapping("/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<RestLoginDTO> getUserRefreshTokenRequest(
            @CookieValue(name = "refresh_token") String refreshTokenFromCookie
    ) throws IdInvalidException {
        // Check valid token
        Jwt jwt = this.securityUtil.checkValidRefreshToken(refreshTokenFromCookie);
        String email = jwt.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshTokenFromCookie, email);
        if (currentUser == null) {
            throw new IdInvalidException("User with refresh token : " + refreshTokenFromCookie
                    + " and email: " + email + " not found!");
        }

        RestLoginDTO restLoginDTO = new RestLoginDTO();
        if (currentUser.getRole() != null
                && currentUser.getRole().getPermissions() != null
                && !currentUser.getRole().getPermissions().isEmpty()
        ) {
            List<String> permissions = currentUser.getRole().getPermissions().stream()
                    .map(Permission::getName)
                    .toList();

            RestLoginDTO.UserLogin.RoleUserLogin roleUserLogin = new RestLoginDTO.UserLogin.RoleUserLogin(
                    currentUser.getRole().getName(),
                    permissions
            );

            restLoginDTO.setUser(new RestLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getName(),
                    currentUser.getEmail(),
                    roleUserLogin
            ));
        } else {
            restLoginDTO.setUser(new RestLoginDTO.UserLogin(
                    currentUser.getId(),
                    currentUser.getName(),
                    currentUser.getEmail(),
                    null
            ));
        }

        // issue new token/set refresh token as cookies
        // Create access & refresh token
        String accessToken = this.securityUtil.createAccessToken(email, restLoginDTO);
        restLoginDTO.setAccessToken(accessToken);

        String newRefreshToken = this.securityUtil.createRefreshToken(email, restLoginDTO);
        this.userService.handleSaveUserRefreshToken(newRefreshToken, email);

        // set cookie
        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(this.refreshTokenExpiration)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(restLoginDTO);
    }

    @PostMapping("/logout")
    @ApiMessage("Logout successfully")
    public ResponseEntity<Void> logoutRequest() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        this.userService.handleSaveUserRefreshToken(null, email);

        ResponseCookie responseCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                .body(null);
    }
}
