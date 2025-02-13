package com.example.config;

import com.example.component.AuthEntryPointJwt;
import com.example.component.AuthTokenFilter;
import com.example.component.StringSetDeserializer;
import com.example.service.impl.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 19:08
 */
@Configuration
@EnableWebSecurity
//@EnableWebFluxSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthEntryPointJwt authEntryPointJwt;

    private final AuthTokenFilter authenticationJwtTokenFilter;



    private static final String[] WHITE_LIST_URL = {
            "/api/user/login",
            "/api/user/register",
            "/api/chat/**",
            "/doc.html",
            "/webjars/**",
            "/v3/**",
            "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth ->
                    auth
                            .requestMatchers(WHITE_LIST_URL).permitAll()
                            .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        ;

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOriginPattern("*"); // 允许任何源
        corsConfig.addAllowedMethod("*"); // 允许任何HTTP方法
        corsConfig.addAllowedHeader("*"); // 允许任何HTTP头
        corsConfig.setAllowCredentials(true); // 允许证书（cookies）
        corsConfig.setMaxAge(3600L); // 预检请求的缓存时间（秒）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // 对所有路径应用这个配置
        return source;
    }

    /**
     * Because the Role passed is of HashSet type, you need to define deserialization by yourself.
     * @return
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Set.class, new StringSetDeserializer());
        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    /**
     * AuthenticationProvider
     * necessary
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager
     * necessary
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
