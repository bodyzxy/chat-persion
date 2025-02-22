package com.example.model.Request;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/20 14:31
 */
public record VideoRequest(Long id, MultipartFile file) {
}
