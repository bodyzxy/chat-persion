package com.example.component;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/8/1 11:29
 */
public enum RedisToken {

    LOGIN_TOKEN("login:token");

    private String token;

    RedisToken(String token){
        this.token = token;
    }

    public String getToken(){
        return token;
    }
}
