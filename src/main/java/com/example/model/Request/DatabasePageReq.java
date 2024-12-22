package com.example.model.Request;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/22 15:01
 */
public record DatabasePageReq(Integer page, Integer pageSize) {
    public DatabasePageReq(){
        this(0, 10);
    }
}
