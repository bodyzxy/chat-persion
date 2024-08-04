package com.example.component;

import com.example.thread.UserHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 拦截个别路径
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/8/4 10:10
 */
@RequiredArgsConstructor
@Component
public class AuthTokenFilterOther extends OncePerRequestFilter {

    private static final List<String> WHITE_LIST_URL = Arrays.asList(
            "/user/login",
            "/user/register",
            "/doc.html",
            "/webjars/**",
            "/doc.html#/**",
            "/v3/**",
            "/swagger-resources/**"
    );
    private final PathMatcher pathMatcher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // 检查请求路径是否在白名单中
        if (WHITE_LIST_URL.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestPath))) {
            filterChain.doFilter(request, response); //如果在白名单中则跳过JWT校验
            return;
        }

        if (UserHolder.getUser() == null){
            System.out.println("This is AuthTokenFilterOther");
            response.sendError(401,"请登录");
            return;
        }
        filterChain.doFilter(request,response);
    }
}
