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
public class Tatile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 120)
    private String name;

    //1表示一级题目，2表示二级题目
    private Integer grade;

    @Size(max = 120)
    private String key;

    //minio视屏链接
    @Column(columnDefinition = "TEXT")
    private String url;
}
