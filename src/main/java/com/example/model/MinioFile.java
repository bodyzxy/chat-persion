package com.example.model;

import com.example.conver.JpaConverterListJson;
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
 * @date 2024/10/5 18:27
 */
@Data
@Table
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MinioFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 文件名
     */
    @Column(columnDefinition = "TEXT")
    private String fileName;

    /**
     * minio文件url
     */
    @Column(columnDefinition = "TEXT")
    private String url;

    /**
     * 该文件分割出的多段向量文本ID
     */
    @Convert(converter = JpaConverterListJson.class)
    @Column(columnDefinition = "TEXT")
    private List<String> vectorId;

    @Column(name = "is_delete",nullable = false)
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 用户ID字段
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User userId;

    /**
     * 创建时间/上传时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

//    public void setUser(User user) {
//        this.userId = user;
//    }
}
