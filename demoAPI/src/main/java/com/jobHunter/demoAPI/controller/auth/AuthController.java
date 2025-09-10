package com.jobHunter.demoAPI.controller.auth;

import com.jobHunter.demoAPI.domain.dto.auth.LoginDTO;
import com.jobHunter.demoAPI.domain.dto.auth.RestLoginDTO;
import com.jobHunter.demoAPI.domain.dto.user.RestUserCreateDTO;
import com.jobHunter.demoAPI.domain.entity.Permission;
import com.jobHunter.demoAPI.domain.entity.User;
import com.jobHunter.demoAPI.service.UserService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenExpiration;

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

    @ApiMessage("Login successfully")
    @PostMapping("/login")
    public ResponseEntity<RestLoginDTO> loginRequest(@Valid @RequestBody LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword()
        );

        Authentication authentication = this.authenticationManagerBuilder.getObject()
                .authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        RestLoginDTO restLoginDTO = new RestLoginDTO();
        User userGetByEmail = this.userService.getUserByEmail(loginDTO.getUsername());
        restLoginDTO.setUser(this.buildUser(userGetByEmail));

        String accessToken = this.securityUtil.createAccessToken(loginDTO.getUsername(), restLoginDTO);
        restLoginDTO.setAccessToken(accessToken);

        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), restLoginDTO);
        this.userService.handleSaveUserRefreshToken(refreshToken, loginDTO.getUsername());

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
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

    @ApiMessage("Register successfully")
    @PostMapping("/register")
    public ResponseEntity<RestUserCreateDTO> registerRequest(@Valid @RequestBody User userRequest) {
        User userCreated = this.userService.createUser(userRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertUserToRestUserCreateDTO(userCreated));
    }

    @ApiMessage("Get account info")
    @GetMapping("/account")
    public ResponseEntity<RestLoginDTO.UserGetAccount> getAccountRequest() {
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        User userGetByEmail = this.userService.getUserByEmail(email);

        RestLoginDTO.UserGetAccount userGetAccount = new RestLoginDTO.UserGetAccount();
        userGetAccount.setUser(this.buildUser(userGetByEmail));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userGetAccount);
    }

    @ApiMessage("Get refresh token")
    @GetMapping("/refresh")
    public ResponseEntity<RestLoginDTO> getRefreshTokenRequest(
            @CookieValue("refresh_token") String refreshTokenFromCookie
    ) {
        Jwt jwtChecked = this.securityUtil.checkValidRefreshToken(refreshTokenFromCookie);
        String emailFromJwt = jwtChecked.getSubject();

        User userGetByEmailAndRefreshToken = this.userService.getUserByRefreshTokenAndEmail(refreshTokenFromCookie, emailFromJwt);
        if (userGetByEmailAndRefreshToken == null) {
            throw new NoSuchElementException("User not found");
        }

        RestLoginDTO restLoginDTO = new RestLoginDTO();
        restLoginDTO.setUser(this.buildUser(userGetByEmailAndRefreshToken));

        String accessTokenNew = this.securityUtil.createAccessToken(emailFromJwt, restLoginDTO);
        restLoginDTO.setAccessToken(accessTokenNew);

        String refreshTokenNew = this.securityUtil.createRefreshToken(emailFromJwt, restLoginDTO);
        this.userService.handleSaveUserRefreshToken(refreshTokenNew, emailFromJwt);

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshTokenNew)
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

    @ApiMessage("Logout successfully")
    @PostMapping("/logout")
    public ResponseEntity<Void> logoutRequest() {
        String email = SecurityUtil.getCurrentUserLogin().orElse(null);
        this.userService.handleSaveUserRefreshToken(email, null);

        ResponseCookie responseCookie = ResponseCookie.from("refresh_token", "")
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

    private RestLoginDTO.UserLogin buildUser(User user) {
        if (user == null) {
            return null;
        }

        if (user.getRole() != null) {
            List<String> permissionList = Optional.ofNullable(user.getRole().getPermissions())
                    .map(permissions -> permissions.stream()
                            .map(Permission::getName)
                            .toList()
                    )
                    .orElse(new ArrayList<>());

            RestLoginDTO.UserLogin.RoleUserLogin roleUserLogin = new RestLoginDTO.UserLogin.RoleUserLogin(
                    user.getRole().getName(),
                    permissionList
            );

            return new RestLoginDTO.UserLogin(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    roleUserLogin
            );
        }

        return new RestLoginDTO.UserLogin(
                user.getId(),
                user.getName(),
                user.getEmail(),
                null
        );
    }
}
