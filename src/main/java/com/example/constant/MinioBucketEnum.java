package com.example.constant;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/20 13:29
 */
public enum MinioBucketEnum {

    CHAT_PERSON("chat-person-test"),
    VIDEO_FILES("movie");

    private String bucketName;
    private MinioBucketEnum(String bucketName){
        this.bucketName = bucketName;
    }
    public String getBucketName(){
        return bucketName;
    }
}
