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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

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
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final List<String> WHITE_LIST_URL = Arrays.asList(
            "/**"
    );
    private final PathMatcher pathMatcher;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 检查请求路径是否在白名单中
        if (WHITE_LIST_URL.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath))) {
            filterChain.doFilter(request, response); //如果在白名单中则跳过JWT校验
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("This is AuthTokenFilter \n");
            response.sendError(401,"请登录");
            return;
        }
        String jwt = authHeader.substring(7);

        if (jwtUtil.validateJwtToken(jwt)){
            String key = RedisToken.LOGIN_TOKEN + jwt;
            Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
            if (userMap.isEmpty()){
                return;
            }
            UserResponse userResponse = BeanUtil.fillBeanWithMap(userMap,new UserResponse(),false);
            User user = userRepository.findByUsername(userResponse.getUsername());
            UserHolder.saveUser(user);

            stringRedisTemplate.expire(key,30, TimeUnit.MINUTES);
        }
        filterChain.doFilter(request,response);
    }
}
