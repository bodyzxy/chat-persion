package com.example.component;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/16 11:38
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(400010, "未登录"),
    USERNAME_IS_ALREADY(400020,"用户已存在"),
    UPDATE_ERROR(400030,"添加失败"),
    ERROR(400,"注册失败"),
    TOKEN_ERROR(400040,"Token is missing"),
    PAGE_ERROR(400050,"page 或 pageSize为空"),
    FILE_ERROR(400060,"文件删除错误"),
    DATABASE_ERROR(400070,"数据库已删除"),
    DATABASE_NULL(400070,"数据库为空"),
    PROMPT_ERROR(400080,"prompt为空"),
    USER_IS_NOT(400090,"用户为空"),
    USERNAME_IS_CREATE(400100,"用户名已存在"),
    EMAIL_IS_CREATE(400101,"邮箱已使用"),
    PASSWORD_ERROR(400102,"重复密码不一致"),
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
