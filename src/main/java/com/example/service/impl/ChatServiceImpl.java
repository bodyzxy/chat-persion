package com.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.common.ApplicationConstant;
import com.example.component.ErrorCode;
import com.example.exception.BusinessException;
import com.example.model.Request.ChatMessage;
import com.example.model.Request.ChatOptions;
import com.example.model.Request.ChatRequest;
import com.example.service.ChatService;
import com.example.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.StreamingChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * 构建OpenAI流式对话客户端
     */
    private StreamingChatClient randomGetStreamingChatClient(ChatOptions chatOptions){
        OpenAiApi openAiApi = new OpenAiApi(defaultApiKey, defaultBaseUrl);
        return new OpenAiChatClient(openAiApi, OpenAiChatOptions.builder()
                .withTemperature(chatOptions.temperature())
                .withModel(chatOptions.model())
                .build());
    }

    //message转换
    private List<Message> transformAiMessage(List<ChatMessage> chatMessages) {
        List<Message> messages = new ArrayList<>();
        for (ChatMessage chatMessage : chatMessages) {
            String role = chatMessage.sender();
            String content = chatMessage.content();
            MessageType aiMessageType = MessageType.fromValue(role);
            switch (aiMessageType){
                case USER -> messages.add(new UserMessage(content));
                case SYSTEM -> messages.add(new SystemMessage(content));
                case ASSISTANT -> messages.add(new AssistantMessage(content));
                case FUNCTION -> messages.add(new FunctionMessage(content));
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
        String documents = listOfSimilarDocuments.stream().map(Document::getContent).collect(Collectors.joining());
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
        if (StrUtil.isBlank(request.prompt())){
            return Flux.error(new RuntimeException(String.valueOf(ErrorCode.PROMPT_ERROR)));
        }

        StreamingChatClient streamingChatClient = randomGetStreamingChatClient(request.chatOptions());
        String prompt = request.prompt();
        List<Message> messages = transformAiMessage(request.messages());
        messages = checkMessageLength(messages, request);
        Message systemMessage = similaritySearc(prompt);
        log.info("-----------------------------"+ systemMessage.toString()+"============================");
        messages.add(0, systemMessage);
        //TODO:将聊天记录在数据库中，还有问答
        return streamingChatClient.stream(new Prompt(messages));
    }
}
