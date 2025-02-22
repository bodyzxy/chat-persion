package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.VideoRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/13 19:18
 */
public interface TitleService {
    BaseResponse getTitle();

    BaseResponse uploadVideo(MultipartFile file, Long id);
}
