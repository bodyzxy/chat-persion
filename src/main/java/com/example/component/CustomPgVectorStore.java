package com.example.component;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 21:44
 */
public class CustomPgVectorStore extends PgVectorStore {
    private Long id;
    private Long databaseId;


    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient) {
        super(jdbcTemplate, embeddingClient);
    }

    public CustomPgVectorStore(JdbcTemplate jdbcTemplate, EmbeddingClient embeddingClient, Long id, Long databaseId) {
        super(jdbcTemplate, embeddingClient);
        this.id = id;
        this.databaseId = databaseId;
    }

    @Override
    public void add(List<Document> documents){
        if (documents == null || documents.isEmpty()) {
            throw new IllegalArgumentException("Documents list cannot be null or empty");
        }

        List<Document> processedDocuments = new ArrayList<>();
        for (Document document : documents) {
            if (document == null) {
                throw new IllegalArgumentException("Document cannot be null");
            }
            // 其他操作
            Map<String, Object> metadata = new HashMap<>(document.getMetadata());
            metadata.put("userId",id);
            metadata.put("databaseId",databaseId);
            Document processedDocument = new Document(
                    document.getId(),
                    document.getContent(),
                    metadata
            );
            processedDocuments.add(processedDocument);
        }
        super.add(processedDocuments);
    }
}
