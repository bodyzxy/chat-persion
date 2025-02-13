package com.example.model.response;

import lombok.Data;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/7 18:43
 */
@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String password;
    private String address;
    private String phone;
    private String email;
    private String rpassword;
    private String introduction;
}
