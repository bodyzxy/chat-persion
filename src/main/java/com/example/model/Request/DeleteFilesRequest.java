package com.example.model.Request;

import java.util.List;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/6 17:55
 */
public record DeleteFilesRequest(Long minioFileId, Long userId) {
}
