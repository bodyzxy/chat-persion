package com.example.controller;

import com.example.component.BaseResponse;
import com.example.model.Request.ChatRequest;
import com.example.service.ChatService;
import com.example.utils.ResultUtils;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/6 16:07
 */
@CrossOrigin(origins = "*", maxAge = 3600)//跨域共享
@RestController
@RequestMapping("/chat")
@Slf4j
@Tag(name = "ChatController",description = "对话接口")
@ApiSupport(author = "bodyzxy")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "stream",description = "流式对话接口")
    @PostMapping("/stream")
    //TODO:参数传递需要加一个userid和databaseID
    public Flux<ChatResponse> stream(@RequestBody ChatRequest request) {
        return chatService.ragChat(request).flatMapSequential(Flux::just);
    }

    @Operation(summary = "text", description = "测试接口")
    @PostMapping("/text")
    public BaseResponse text(@RequestBody ChatRequest request) {
        return ResultUtils.success("请求成功");
    }


}
