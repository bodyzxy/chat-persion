package com.example.repository;

import com.example.model.Role;
import com.example.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/7/15 18:24
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT u.roles FROM User u WHERE u.id = :userId")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Long findIdByUsername(@Param("username") String username);
}
