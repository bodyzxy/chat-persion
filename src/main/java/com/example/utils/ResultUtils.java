package com.example.utils;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/16 11:43
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data) {return new BaseResponse<>(0,"ok",data);}

    /**
     * 失败
     * @param code
     * @param msg
     * @return
     *
     */
    public static BaseResponse error(Integer code, String msg) {return new BaseResponse<>(code,msg,null);}

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){return new BaseResponse<>(errorCode.getCode(),errorCode.getMsg(),null);}

    /**
     * 失败
     * @param msg
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String msg){return new BaseResponse<>(errorCode.getCode(),msg,null);}
}
