package com.example.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 19:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, String username, List<String> roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
    }
}