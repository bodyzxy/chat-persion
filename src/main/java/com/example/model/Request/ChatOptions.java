package com.example.model.Request;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/6 16:56
 */
public record ChatOptions(String model,Integer maxHistoryLength,String chatType,Float temperature) {
    /**
     *
     * @param model LLM model. enum class LLMModels
     * @param maxHistoryLength
     * @param chatType RAG or simple
     * @param temperature
     */
}
