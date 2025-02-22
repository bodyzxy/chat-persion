package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程标题信息
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/13 16:39
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Title {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 120)
    private String name;

    //优先级
    private Integer grade;

    @Size(max = 120)
    private String key;

    //minio视屏链接
    @Column(columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String content;
}
