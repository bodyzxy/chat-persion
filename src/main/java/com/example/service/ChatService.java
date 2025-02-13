package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.ChatRequest;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/31 20:30
 */
public interface ChatService {
    Flux<ChatResponse> ragChat(ChatRequest request);

    BaseResponse common(ChatRequest request);

    BaseResponse hotBook(ChatRequest request);

    BaseResponse hotTitle(ChatRequest request);

    BaseResponse talk(ChatRequest request);
}
