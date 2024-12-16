package com.example.model.response;

import com.example.model.Comment;
import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 22:05
 */
@Data
public class CommentResponse {
    /**
     * 所属一级评论的id，如果当前评论为一级，则为0
     */
    private Long pid;

    /**
     * 评论所属文章id
     */
    private Long databaseId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 该条评论的作者
     */
    private String userName;

    /**
     * 对谁回复，一级评论可以为null
     */
    private String toUserName;

    /**
     * 创建时间
     */
    private Date createTime;

}
