package com.example.component;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/16 11:35
 */
@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {
    private int code;

    private String msg;

    private T data;

    public BaseResponse(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseResponse(int code, T data) {this(code,null,data);}
    public BaseResponse(ErrorCode errorCode) {this(errorCode.getCode(), errorCode.getMsg(), null);}
}
