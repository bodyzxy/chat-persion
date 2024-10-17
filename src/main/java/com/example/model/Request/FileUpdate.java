package com.example.model.Request;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/17 21:04
 */
public record FileUpdate(Long databaseId, MultipartFile file) {
}
