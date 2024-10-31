package com.example.model.Request;

import com.google.protobuf.Message;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/6 16:59
 */
public record ChatRequest(List<ChatMessage> messages,ChatOptions chatOptions,String prompt) {
    /**
     * @param messages history context message
     * @param chatOptions chat settings
     * @param prompt user's question
     */
}
