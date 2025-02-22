package com.example.repository;

import com.example.model.MinioFile;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/5 18:37
 */
public interface MinioFileRepository extends JpaRepository<MinioFile, Long> {
    Page<MinioFile> findByUserIdContaining(Long keyword, Pageable pageable);

    @Query("SELECT m.fileName FROM MinioFile m WHERE m.databaseId = :databaseId")
    List<String> findNamesByDatabaseId(@Param("databaseId") Long databaseId);

//    Page<MinioFile> findAllByUserId(Long userId);
}
