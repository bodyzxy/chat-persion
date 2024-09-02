package com.example.model.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 20:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfRequest {
    private String username;
    private MultipartFile file;
}
