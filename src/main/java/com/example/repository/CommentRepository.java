package com.example.repository;

import com.example.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 21:26
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
