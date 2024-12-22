package com.example.model.Request;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/22 16:32
 */
public record UserComment(
        Long pid,
        Long databaseId,
        String content,
        String userName,
        String toUserName
) {
}
