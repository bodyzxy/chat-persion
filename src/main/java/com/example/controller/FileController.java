package com.example.controller;

import com.example.component.BaseResponse;
import com.example.component.ErrorCode;
import com.example.model.Request.QueryFileRequest;
import com.example.service.PdfService;
import com.example.utils.ResultUtils;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/5 17:13
 */
@CrossOrigin(origins = "*", maxAge = 3600)//跨域共享
@RestController
@RequestMapping("/file")
@Slf4j
@Tag(name = "FileController",description = "文件操作接口")
@ApiSupport(author = "bodyzxy")
@RequiredArgsConstructor
public class FileController {
    private final PdfService pdfService;

    @PostMapping("/update")
    @Operation(description = "上传文件")
    public BaseResponse updatePdf(@RequestParam("file") MultipartFile file){
        return pdfService.updatePdf(file);
    }

    @GetMapping("/contents")
    @Operation(description = "文件查询")
    public BaseResponse contents(QueryFileRequest request){
        if (request.page() == null || request.pageSize() == null){
            return ResultUtils.error(ErrorCode.PAGE_ERROR);
        }
        return pdfService.contents(request);
    }

    @DeleteMapping("/deleteFile")
    @Operation(description = "删除文件")
    public BaseResponse deleteFile(Long id){
        return pdfService.deleteFile(id);
    }


}
