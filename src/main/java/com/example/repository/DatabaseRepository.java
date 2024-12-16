package com.example.repository;

import com.example.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 21:07
 */
public interface DatabaseRepository extends JpaRepository<Database, Long> {
    List<Database> findByIsPublicTrue();

    List<Database> findAllByUserId(Long userId);
}
