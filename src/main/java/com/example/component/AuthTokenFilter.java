package com.example.component;

import cn.hutool.core.bean.BeanUtil;
import com.example.model.User;
import com.example.model.response.UserResponse;
import com.example.repository.UserRepository;
import com.example.service.impl.UserDetailsServiceImpl;
import com.example.thread.UserHolder;
import com.example.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * filter
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 18:50
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("执行Filter-----------------------");
        try{
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AuthenticationException("未登录");
            }

            String jwt = authHeader.substring(7);

            if (jwtUtil.validateJwtToken(jwt)){
                String key = RedisToken.LOGIN_TOKEN + jwt;
                Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
                if (userMap.isEmpty()){
                    UserHolder.removeUser();
                    throw new AuthenticationException("登录过期，请重新登录");
                }
                UserResponse userResponse = BeanUtil.fillBeanWithMap(userMap,new UserResponse(),false);
                User user = userRepository.findByUsername(userResponse.getUsername());
                if(UserHolder.getUser() == null){
                    UserHolder.saveUser(user);
                }

                //将身份验证信息存入SecurityContext以便后续请求识别---filterChain中会使用到如果不设置将无法继续执行
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                stringRedisTemplate.expire(key,30, TimeUnit.MINUTES);
                log.info("执行通过-=-=-=-=-=11111111111111111111111111111");
            }
        } catch (Exception e){
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request,response);
    }
}
