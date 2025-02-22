package com.example.repository;

import com.example.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 21:07
 */
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    Page<Database> findByIsPublicTrueAndIsDeletedFalse(Pageable pageable);

    List<Database> findAllByUserId(Long userId);
}
