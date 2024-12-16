package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 17:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table
@Builder
@Entity
public class Database {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String name;

    private Date date;

    // 用户ID字段
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "file_id",nullable = true)
    private Long fileId;

    @Column(name = "is_delete",nullable = false)
    private Boolean isDeleted = false;

    //是否公开
    @Column(name = "is_public",nullable = false)
    private Boolean isPublic = false;

    //收藏数量
    @Column(name = "star_number",nullable = false)
    private Integer starNumber=0;

    public Database(User user, String name, Date date) {
        this.user = user;
        this.name = name;
        this.date = date;
    }
}
