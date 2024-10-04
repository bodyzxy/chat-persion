package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.PdfRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 20:59
 */
public interface PdfService {
    BaseResponse updatePdf(MultipartFile file);
}
