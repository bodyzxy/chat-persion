package com.example.controller;

import com.example.component.BaseResponse;
import com.example.model.Request.UserComment;
import com.example.service.CommentService;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 21:14
 */
@RestController
@RequestMapping("/comment")
@Tag(name="CommentController", description = "评论接口")
@Slf4j
@RequiredArgsConstructor
@ApiSupport(author = "bodyzxy")
public class CommentController {

    private final CommentService commentService;

    /**
     * 获取数据库评论
     * @param id 数据库ID
     * @return
     */
    @GetMapping("/getDatabaseComment/{id}")
    @Operation(description = "获取数据库评论")
    public BaseResponse getDatabaseComment(@PathVariable("id") Long id) {
        return commentService.getDatabaseComment(id);
    }

    @PostMapping("/comment")
    @Operation(description = "评论")
    public BaseResponse addComment(@RequestBody UserComment userComment) {
        return commentService.addComment(userComment);
    }


}
