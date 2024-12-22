package com.example.controller;

import com.example.component.BaseResponse;
import com.example.model.Request.ChangeIntroduction;
import com.example.model.Request.RegisterRequest;
import com.example.model.Request.SignInRequest;
import com.example.model.Request.UserInfo;
import com.example.service.UserService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 19:48
 */
@CrossOrigin(origins = "*",maxAge = 3600) //跨域共享
@RestController
@RequestMapping("/user")
@Slf4j
@Tag(name="AccountController",description = "用户账户的操作接口")
@ApiSupport(author = "bodyzxy")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    @Operation(description = "登录")
    public BaseResponse authenticateUser(@Valid @RequestBody SignInRequest signInRequest){
        return userService.login(signInRequest);
    }

    @PostMapping("/register")
    @Operation(description = "注册")
    public BaseResponse authenticateRegister(@RequestBody @Valid RegisterRequest registerRequest){
        return userService.register(registerRequest);
    }

    @GetMapping("/logout")
    @Operation(description = "退出")
    public BaseResponse logout(HttpServletRequest request){
        return userService.logout(request);
    }

    @PostMapping("/changeIntroduction")
    @Operation(description = "修改个人简介")
    public BaseResponse changeIntroduction(@RequestBody @Valid ChangeIntroduction changeIntroduction){
        return userService.changeIntroduction(changeIntroduction);
    }

    @PostMapping("/changeUserInfo")
    @Operation(description = "修改个人信息")
    public BaseResponse changeUserInfo(@RequestBody UserInfo changeUserInfo){
        return userService.changeUserInfo(changeUserInfo);
    }

    @GetMapping("/test")
    @Operation(description = "测试")
    public String test(){
        return "test";
    }
}
