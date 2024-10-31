package com.example.service;

import com.example.component.BaseResponse;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/30 21:02
 */
public interface DatabaseService {
    Long createDatabase(Long id);

    BaseResponse shareDatabase(Long id);

    BaseResponse delete(Long id);
}
