package com.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.common.ApplicationConstant;
import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.constant.HotBook;
import com.example.constant.HotGithub;
import com.example.constant.HotTitle;
import com.example.exception.BusinessException;
import com.example.model.Request.ChatMessage;
import com.example.model.Request.ChatOptions;
import com.example.model.Request.ChatRequest;
import com.example.service.ChatService;
import com.example.service.PdfService;
import com.example.utils.ResultUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.ApiKey;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/31 20:30
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);
    @Value("${spring.ai.openai.api-key}")
    private String defaultApiKey;
    @Value("${spring.ai.openai.base-url}")
    private String defaultBaseUrl;
    private PdfService pdfService;

    @Autowired
    public ChatServiceImpl(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    ApiKey customApiKey = new ApiKey() {
        @Override
        public String getValue() {
            // Custom logic to retrieve API key
            return defaultApiKey;
        }
    };

    /**
     * 构建OpenAI流式对话客户端
     */
    private OpenAiChatModel randomGetStreamingChatClient(ChatOptions chatOptions){
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl(defaultBaseUrl)
                .apiKey(customApiKey)
                .build();
        return new OpenAiChatModel(openAiApi, OpenAiChatOptions.builder()
                .temperature(Double.valueOf(chatOptions.temperature()))
                .model(chatOptions.model())
                .build());
    }

    //message转换
    private List<Message> transformAiMessage(List<ChatMessage> chatMessages) {
        List<Message> messages = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            String role = chatMessage.sender();
            String content = chatMessage.content();
            if(!Objects.equals(role, "assistant") && !Objects.equals(role, "system") && !Objects.equals(role, "user")){
                role = "user";
            }
            MessageType aiMessageType = MessageType.fromValue(role);
            switch (aiMessageType){
                case USER -> messages.add(new UserMessage(content));
                case SYSTEM -> messages.add(new SystemMessage(content));
                case ASSISTANT -> messages.add(new AssistantMessage(content));
                default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"对话列表存在未知类别:" + role);
            }
        }
        return messages;
    }

    //保证消息长度在配置长度范围内
    private List<Message> checkMessageLength(List<Message> messages, ChatRequest chatRequest){
        if (!messages.isEmpty()&&messages.get(0).getMessageType() == MessageType.SYSTEM){
            messages.remove(0);
        }
        Integer maxMessageLength = chatRequest.chatOptions().maxHistoryLength();
        int currentMessageLength = messages.size();
        if (currentMessageLength > maxMessageLength){
            messages = messages.subList(currentMessageLength-maxMessageLength, currentMessageLength);
        }
        return messages;
    }
    // 向量数据库检索 返回系统提示信息（该信息包含了查询到的一组文档）
    private Message similaritySearc(String prompt){
        VectorStore vectorStore = pdfService.randomVectorStore();
        //TODO: 此处修改为自己的similaritySearch进行检索
        List<Document> listOfSimilarDocuments = vectorStore.similaritySearch(prompt);
        //将Document列表中的每个元素的content内容进行拼接获得documents
        String documents = listOfSimilarDocuments.stream().map(Document::getText).collect(Collectors.joining());
        //使用Spring AI提供的模版凡事构建SystemMessage对象
        Message systemMessage = new SystemPromptTemplate(ApplicationConstant.SYSTEM_PROMPT).createMessage(Map.of("documents", documents));
        return systemMessage;
    }

    /**
     * 流式RAG对话
     * @param request
     * @return
     */
    @Override
    public Flux<ChatResponse> ragChat(ChatRequest request) {
        if (StrUtil.isBlank(request.prompt())) {
            return Flux.error(new RuntimeException(String.valueOf(ErrorCode.PROMPT_ERROR)));
        }

        SecurityContext context = SecurityContextHolder.getContext(); // 1️⃣ 先获取 SecurityContext

        OpenAiChatModel streamingChatClient = randomGetStreamingChatClient(request.chatOptions());
        String prompt = request.prompt();
        List<Message> messages = transformAiMessage(request.messages());
        messages = checkMessageLength(messages, request);
        Message systemMessage = similaritySearc(prompt);
        log.info("-----------------------------" + systemMessage + "============================");
        messages.add(0, systemMessage);

        List<Message> finalMessages = messages;
        return Flux.deferContextual(ctx -> {
            SecurityContextHolder.setContext(context); // 2️⃣ 在 Reactor 上下文中恢复 SecurityContext
            return streamingChatClient.stream(new Prompt(finalMessages));
        }).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context))); // 3️⃣ 绑定 SecurityContext 到 Reactor
    }

    @Override
    public BaseResponse common(ChatRequest request) {
        try {
            // 使用流式对话客户端，初始化时使用的是传入的 ChatRequest 中的配置
            OpenAiChatModel streamingChatClient = randomGetStreamingChatClient(request.chatOptions());

            // 准备消息
            List<Message> messages = transformAiMessage(request.messages());
            messages = checkMessageLength(messages, request);
            HotGithub prompt = new HotGithub();

            // 直接将用户的提示作为系统消息，不需要向量检索
            Message systemMessage = new SystemMessage(prompt.getPrompt()); // 使用用户的提示作为系统消息
            messages.add(0, systemMessage); // 将系统消息添加到消息列表的开头

            // 使用生成的消息进行请求
            ChatResponse chatResponse = streamingChatClient.call(new Prompt(messages)); // 这是同步请求

            // Process or return the resources as needed
            return ResultUtils.success(chatResponse.getResult().getOutput().getText());
        } catch (Exception e) {
            // 异常处理
            log.error("Error while communicating with OpenAI: ", e);
            return ResultUtils.error(ErrorCode.NOT_ERROR);
        }
    }

    @Override
    public BaseResponse hotBook(ChatRequest request) {
        try{
            OpenAiChatModel streamingChatClient = randomGetStreamingChatClient(request.chatOptions());
            List<Message> messages = transformAiMessage(request.messages());
            messages = checkMessageLength(messages, request);

            HotBook prompt = new HotBook();
            Message systemMessage = new SystemMessage(prompt.getPrompt());
            messages.add(0, systemMessage);

            ChatResponse chatResponse = streamingChatClient.call(new Prompt(messages));

            return ResultUtils.success(chatResponse.getResult().getOutput().getText());
        }catch (Exception e){
            // 异常处理
            log.error("Error while communicating with OpenAI: ", e);
            return ResultUtils.error(ErrorCode.NOT_ERROR);
        }
    }

    @Override
    public BaseResponse hotTitle(ChatRequest request) {
        try{
            OpenAiChatModel streamingChatClient = randomGetStreamingChatClient(request.chatOptions());
            List<Message> messages = transformAiMessage(request.messages());
            messages = checkMessageLength(messages, request);
            HotTitle prompt = new HotTitle();
            Message systemMessage = new SystemMessage(prompt.getPrompt());
            messages.add(0, systemMessage);
            ChatResponse chatResponse = streamingChatClient.call(new Prompt(messages));
            return ResultUtils.success(chatResponse.getResult().getOutput().getText());
        }catch (Exception e){
            log.error("Error while communicating with OpenAI: ", e);
            return ResultUtils.error(ErrorCode.NOT_ERROR);
        }
    }

    @Override
    public BaseResponse talk(ChatRequest request) {

        try{
            OpenAiChatModel streamingChatClient = randomGetStreamingChatClient(request.chatOptions());
            List<Message> messages = transformAiMessage(request.messages());
            messages = checkMessageLength(messages, request);
            Message systemMessage = new SystemMessage("你必须使用中文回答,"+request.prompt());
            messages.add(0, systemMessage);
            ChatResponse chatResponse = streamingChatClient.call(new Prompt(messages));
            return ResultUtils.success(chatResponse.getResult().getOutput().getText());
        }catch (Exception e){
            log.error("Error while communicating with OpenAI: ", e);
            return ResultUtils.error(ErrorCode.NOT_ERROR);
        }
    }

}
