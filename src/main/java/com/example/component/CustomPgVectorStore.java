package com.example.component;

import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 21:44
 */
public class CustomPgVectorStore extends PgVectorStore {
    private Long id;
    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, OpenAiEmbeddingModel embeddingClient) {
        super(jdbcTemplate, embeddingClient);
    }

    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, OpenAiEmbeddingModel embeddingClient, Long id) {
        super(jdbcTemplate, embeddingClient);
        this.id = id;
    }

    @Override
    public void add(List<Document> documents){
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("Documents list cannot be null or empty");
        }

        for (Document document : documents) {
            if (document == null) {
                throw new IllegalArgumentException("Document cannot be null");
            }
            document.getMetadata().put("userId", id);
            // 其他操作
        }
    }
}
