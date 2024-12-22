package com.example.service;

import com.example.component.BaseResponse;
import com.example.model.Request.UserComment;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/12/2 21:17
 */
public interface CommentService {
    BaseResponse getDatabaseComment(Long id);

    BaseResponse addComment(UserComment userComment);
}
