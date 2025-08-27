package com.jobHunter.demoAPI.util.security;

import com.jobHunter.demoAPI.domain.dto.auth.RestLoginDTO;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class SecurityUtil {

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${jobhunter.jwt.base64-secret}")
    private String jwtKey;

    @Value("${jobhunter.jwt.access-token-validity-in-seconds}")
    private long accessTokenExpiration;

    @Value("${jobhunter.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final JwtEncoder jwtEncoder;

    public SecurityUtil(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(this.jwtKey).decode();
        return new SecretKeySpec(
                keyBytes,
                0,
                keyBytes.length,
                JWT_ALGORITHM.getName()
        );
    }

    public Jwt checkValidRefreshToken(String token) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(getSecretKey())
                .macAlgorithm(JWT_ALGORITHM)
                .build();
        try {
            return decoder.decode(token);
//            System.out.println(">>> Refresh token valid!");
        } catch (Exception ex) {
            System.out.println(">>> Refresh token error: " + ex.getMessage());
            throw ex;
        }
    }

    public String createAccessToken(String email, RestLoginDTO restLoginDTO) {
        Instant now = Instant.now();
        // lấy thời điểm hiện tại (trên) + số giây token có hiệu lực
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        RestLoginDTO.UserInsideToken userInsideToken = new RestLoginDTO.UserInsideToken(
                restLoginDTO.getUser().getId(),
                restLoginDTO.getUser().getName(),
                restLoginDTO.getUser().getEmail()
        );

        // payload
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .build();

        // header
        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
                .getTokenValue();
    }

    public String createRefreshToken(String email, RestLoginDTO restLoginDTO) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

        RestLoginDTO.UserInsideToken userInsideToken = new RestLoginDTO.UserInsideToken(
                restLoginDTO.getUser().getId(),
                restLoginDTO.getUser().getName(),
                restLoginDTO.getUser().getEmail()
        );

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(email)
                .claim("user", userInsideToken)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

        return this.jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet))
                .getTokenValue();
    }

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }

    /**
     * Get the JWT of the current user.
     *
     * @return the JWT of the current user.
     */
    public static Optional<String> getCurrentUserJWT() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication())
                .filter(authentication -> authentication.getCredentials() instanceof String)
                .map(authentication -> (String) authentication.getCredentials());
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise.
     */
    // public static boolean isAuthenticated() {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return authentication != null && getAuthorities(authentication).noneMatch(AuthoritiesConstants.ANONYMOUS::equals);
    // }

    /**
     * Checks if the current user has any of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has any of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserAnyOfAuthorities(String... authorities) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     return (
    //         authentication != null && getAuthorities(authentication).anyMatch(authority -> Arrays.asList(authorities).contains(authority))
    //     );
    // }

    /**
     * Checks if the current user has none of the authorities.
     *
     * @param authorities the authorities to check.
     * @return true if the current user has none of the authorities, false otherwise.
     */
    // public static boolean hasCurrentUserNoneOfAuthorities(String... authorities) {
    //     return !hasCurrentUserAnyOfAuthorities(authorities);
    // }

    /**
     * Checks if the current user has a specific authority.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    // public static boolean hasCurrentUserThisAuthority(String authority) {
    //     return hasCurrentUserAnyOfAuthorities(authority);
    // }

    // private static Stream<String> getAuthorities(Authentication authentication) {
    //     return authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority);
    // }

}
