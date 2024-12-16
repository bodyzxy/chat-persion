package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/31 21:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long databaseId;

    private Long userId;

    private String content;
}
