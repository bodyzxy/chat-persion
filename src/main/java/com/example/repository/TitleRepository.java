package com.example.repository;

import com.example.model.Title;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/13 19:21
 */
public interface TitleRepository extends JpaRepository<Title, Long> {
}
