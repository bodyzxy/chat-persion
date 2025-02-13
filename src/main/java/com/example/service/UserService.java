package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.ChangeIntroduction;
import com.example.model.Request.RegisterRequest;
import com.example.model.Request.SignInRequest;
import com.example.model.Request.UserInfo;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/17 14:02
 */
public interface UserService {
    BaseResponse register(RegisterRequest registerRequest);

    BaseResponse login(SignInRequest signInRequest);

    BaseResponse logout(HttpServletRequest request);

    BaseResponse changeIntroduction(ChangeIntroduction changeIntroduction);

    BaseResponse changeUserInfo(UserInfo changeUserInfo);

    BaseResponse getUserInfo(String name);
}
