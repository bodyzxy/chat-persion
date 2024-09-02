package com.example.component;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
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
    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
        super(jdbcTemplate, embeddingClient);
    }

    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient, Long id) {
        super(jdbcTemplate, embeddingClient);
        this.id = id;
    }

    @Override
    public void add(List<Document> documents){
        for (Document document : documents){
            document.getMetadata().put("userId",id);
            //TODO id存入后其他操作
        }
    }
}
