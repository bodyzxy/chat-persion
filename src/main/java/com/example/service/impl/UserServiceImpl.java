package com.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.component.RedisToken;
import com.example.model.ERole;
import com.example.model.Request.ChangeIntroduction;
import com.example.model.Request.RegisterRequest;
import com.example.model.Request.SignInRequest;
import com.example.model.Request.UserInfo;
import com.example.model.Role;
import com.example.model.User;
import com.example.model.response.JwtResponse;
import com.example.model.response.UserInfoResponse;
import com.example.model.response.UserResponse;
import com.example.repository.RoleRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.thread.UserHolder;
import com.example.utils.JwtUtil;
import com.example.utils.ResultUtils;
import jakarta.servlet.http.HttpServletRequest;
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

        // 2. 校验密码
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            return ResultUtils.error(ErrorCode.PASSWORD_ERROR);
        }
        UserResponse userResponse = BeanUtil.copyProperties(user,UserResponse.class);
        Map<String,Object> userMap = BeanUtil.beanToMap(userResponse,new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName,fieldValue) -> fieldValue.toString()));

        String jwt = jwtUtils.generateToken(userMap);

        String tokenKey = RedisToken.LOGIN_TOKEN + jwt;
        stringRedisTemplate.opsForHash().putAll(tokenKey,userMap);
        stringRedisTemplate.expire(tokenKey,24, TimeUnit.HOURS);
        Set<Role> roles = userRepository.findRolesByUserId(userResponse.getId());
        List<String> role = roles.stream().map(Role::getRole).collect(Collectors.toList());



        return ResultUtils.success(
                new JwtResponse(
                        jwt,
                        userResponse.getUsername(),
                        user.getId(),
                        role
                )
        );
    }

    @Override
    public BaseResponse logout(HttpServletRequest request) {
        //从请求头中获取JWT
        String  jwt = request.getHeader("Authorization");
        //检查JWT是否存在
        if (jwt == null || jwt.isEmpty()){
            return ResultUtils.error(ErrorCode.TOKEN_ERROR);
        }
        //去掉“Bearer ”前缀
        if (jwt.startsWith("Bearer ")){
            jwt = jwt.substring(7);
        }
        //构造Redis中存储该用户信息的key
        String tokenKey = RedisToken.LOGIN_TOKEN + jwt;
        //删除Redis中对应的记录
        Boolean result = stringRedisTemplate.delete(tokenKey);
        //删除线程中的用户
        UserHolder.removeUser();
        if (Boolean.TRUE.equals(result)){
            return ResultUtils.success("退出成功");
        }else {
            return ResultUtils.error(ErrorCode.TOKEN_ERROR);
        }
    }

    @Override
    public BaseResponse changeIntroduction(ChangeIntroduction changeIntroduction) {
        Optional<User> user = userRepository.findById(changeIntroduction.userId());
        if(!user.isPresent()){
            return ResultUtils.error(ErrorCode.USER_IS_NOT);
        }else {
            User user1 = user.get();
            user1.setIntroduction(changeIntroduction.introduction());
            userRepository.save(user1);
        }
        return ResultUtils.success("修改成功");
    }

    @Override
    public BaseResponse changeUserInfo(UserInfo changeUserInfo) {
        Optional<User> userOptional = userRepository.findById(changeUserInfo.id());
        if(!userOptional.isPresent()){
            return ResultUtils.error(ErrorCode.USER_IS_NOT);
        }
        User user = userOptional.get();
        //姓名唯一
        boolean isNameTaken = userRepository.existsByUsernameAndIdNot(changeUserInfo.username(), changeUserInfo.id());
        if (isNameTaken){
            return ResultUtils.error(ErrorCode.USERNAME_IS_ALREADY);
        }
        //邮箱唯一
        boolean isEmailTaken = userRepository.existsByEmailAndIdNot(changeUserInfo.email(),changeUserInfo.id());
        if (isEmailTaken){
            return ResultUtils.error(ErrorCode.EMAIL_IS_CREATE);
        }
        //密码一致性
        if (!changeUserInfo.password().equals(changeUserInfo.rpassword())){
            return ResultUtils.error(ErrorCode.PASSWORD_ERROR);
        }

        user.setUsername(changeUserInfo.username());
        user.setPassword(changeUserInfo.password());
        user.setEmail(changeUserInfo.email());
        user.setAddress(changeUserInfo.address());
        user.setPhone(changeUserInfo.phone());
        userRepository.save(user);
        return ResultUtils.success("修改成功");
    }

    @Override
    public BaseResponse getUserInfo(String name) {
        User user = userRepository.findByUsername(name);
        if (user == null){
            return ResultUtils.error(ErrorCode.USER_IS_NOT);
        }
        User user2 = UserHolder.getUser();
        if (!Objects.equals(user.getId(), user2.getId())){
            user.setPassword(null);
        }
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        BeanUtil.copyProperties(user,userInfoResponse);
        return ResultUtils.success(userInfoResponse);
    }
}
