package com.example.model.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 19:52
 */
@Data
public class SignInRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
