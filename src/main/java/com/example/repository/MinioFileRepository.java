package com.example.repository;

import com.example.model.MinioFile;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/5 18:37
 */
public interface MinioFileRepository extends JpaRepository<MinioFile, Long> {
    Page<MinioFile> findByUserIdContaining(Long keyword, Pageable pageable);

    Page<MinioFile> findAllByUserId(Long userId);
}
