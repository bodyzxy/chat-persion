package com.example.model.Request;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/14 19:27
 */
public record ChatDataBaseRequest(List<ChatMessage> messages, ChatOptions chatOptions, String prompt, Long databaseId) {
}
