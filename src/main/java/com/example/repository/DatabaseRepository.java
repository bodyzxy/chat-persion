package com.example.repository;

import com.example.model.Database;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 21:07
 */
public interface DatabaseRepository extends JpaRepository<Database, Long> {
}
