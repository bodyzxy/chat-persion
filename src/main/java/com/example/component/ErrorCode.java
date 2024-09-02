package com.example.component;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/16 11:38
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    USERNAME_IS_ALREADY(400020,"用户以存在"),
    UPDATE_ERROR(40030,"添加失败"),
    NO_AUTH_ERROR(40101, "无权限");

    private final int code;
    private final String msg;

    ErrorCode(int code, String message) {
        this.code = code;
        this.msg= message;
    }

    public int getCode() {return code;}
    public String getMsg() {return msg;}
}
