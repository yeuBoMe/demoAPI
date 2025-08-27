package com.jobHunter.demoAPI.domain.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RestLoginDTO {

    @JsonProperty("access_token")
    private String accessToken;

    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserLogin {
        private Long id;
        private String name;
        private String email;
        private RoleUserLogin role;

        @Getter
        @Setter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class RoleUserLogin {
            private String name;
            private List<String> permissions;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserGetAccount {
        private UserLogin user;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInsideToken {
        private Long id;
        private String name;
        private String email;
    }
}
