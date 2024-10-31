package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/9 20:56
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Size(max = 50)
    private String phone;

    private String address;

    @Column(name = "info_user",nullable = true)
    private String infoUser;

    @Column(name = "is_delete",nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "is_enable",nullable = false)
    private Boolean isEnabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

//    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Column(name = "minio_files", nullable = true)
//    private List<MinioFile> minioFiles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Database> databases;

    public void addDatabaseAndMinioFile(Database database) {
        // 确保数据库列表初始化
        if (this.databases == null) {
            this.databases = new ArrayList<>();
        }
        // 确保Minio文件列表初始化
//        if (this.minioFiles == null) {
//            this.minioFiles = new ArrayList<>();
//        }

        // 添加Database
        this.databases.add(database);
//        database.setUser(this); // 反向设置关系

        // 添加MinioFile
//        this.minioFiles.add(minioFile);
//        minioFile.setUser(this); // 反向设置关系
    }


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
