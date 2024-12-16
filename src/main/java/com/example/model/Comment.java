package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 评论实体
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 21:11
 */
@Data
@Table(name = "t_comment")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Comment {
    /**
     * 评论id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属一级评论的id，如果当前评论为一级，则为0
     */
    private Long pid;

    /**
     * 评论所属文章id
     */
    @Column(name = "database_id")
    private Long databaseId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 该条评论的作者
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 对谁回复，一级评论可以为null
     */
    @Column(name = "to_user_name")
    private String toUserName;

    /**
     * 当前评论的点赞数
     */
    @Column(name = "likes_count")
    private Integer likesCount;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 该评论下的回复，非数据库字段，用 @Transient
     */
    @Transient
    private List<Comment> replies = new ArrayList<>();
}
