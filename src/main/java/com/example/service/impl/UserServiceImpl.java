package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.component.RedisToken;
import com.example.model.ERole;
import com.example.model.Request.RegisterRequest;
import com.example.model.Request.SignInRequest;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.response.JwtResponse;
import com.example.model.response.UserResponse;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.utils.JwtUtil;
import com.example.utils.ResultUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/17 14:03
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 注册
     * @param registerRequest
     * @return
     */
    @Override
    public BaseResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())){
            return ResultUtils.error(ErrorCode.USERNAME_IS_ALREADY);
        }
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword())
        );

        Set<String> registerRoel = registerRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (registerRoel == null || registerRoel.isEmpty()){
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found!"));
            roles.add(userRole);
        } else {
            registerRoel.forEach(
                    role -> {
                        switch (role){
                            case "admin":
                                Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                        .orElseThrow(() -> new RuntimeException("Error: Role not admin!"));
                                roles.add(adminRole);
                                break;
                            case "mod":
                                Role modRole = roleRepository.findByName(ERole.MODERATOR)
                                        .orElseThrow(() -> new RuntimeException("Error: Role not mod!"));
                                roles.add(modRole);
                                break;
                            default:
                                Role userRole = roleRepository.findByName(ERole.USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role not found!"));
                                roles.add(userRole);
                        }
                    }
            );
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResultUtils.success("注册成功");
    }

    /**
     * 登录
     * @param signInRequest
     * @return
     */
    @Override
    public BaseResponse login(SignInRequest signInRequest) {
        User user = userRepository.findByUsername(signInRequest.getUsername());
        UserResponse userResponse = BeanUtil.copyProperties(user,UserResponse.class);
        Map<String,Object> userMap = BeanUtil.beanToMap(userResponse,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName,fieldValue) -> fieldValue.toString()));

        String jwt = jwtUtils.generateToken(userMap);

        String tokenKey = RedisToken.LOGIN_TOKEN + jwt;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(tokenKey,30, TimeUnit.MINUTES);
        Set<Role> roles = userRepository.findRolesByUserId(userResponse.getId());
        List<String> role = roles.stream().map(Role::getRole).collect(Collectors.toList());



        return ResultUtils.success(
                new JwtResponse(
                        jwt,
                        userResponse.getUsername(),
                        role
                )
        );
    }
}
