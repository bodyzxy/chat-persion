package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.DeleteFilesRequest;
import com.example.model.Request.FileUpdate;
import com.example.model.Request.QueryFileRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/9/2 20:59
 */
public interface PdfService {
    BaseResponse updatePdf(FileUpdate fileUpdate);

    BaseResponse contents(QueryFileRequest request);

    BaseResponse deleteFile(Long id);

//    BaseResponse contentsAll(Long id);
}
