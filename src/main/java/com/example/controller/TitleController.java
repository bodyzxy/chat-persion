package com.example.controller;

import com.example.component.BaseResponse;
import com.example.model.Request.VideoRequest;
import com.example.service.TitleService;
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
 * @date 2025/2/13 19:13
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/title")
@Slf4j
@Tag(name = "TitleController", description = "课程接口")
@ApiSupport(author = "bodyzxy")
@RequiredArgsConstructor
public class TitleController {

    private final TitleService titleService;

    @Operation(summary = "getTitle", description = "获取课程接口")
    @GetMapping("/getTitle")
    public BaseResponse getTitle() {
        return titleService.getTitle();
    }

    @Operation(summary = "uploadVideo", description = "上传视屏")
    @PostMapping(value = "/uploadVideo", consumes = "multipart/form-data")
    public BaseResponse uploadVideo(@RequestPart("file") MultipartFile file, @RequestParam("id") Long id) {
        return titleService.uploadVideo(file,id);
    }
}
