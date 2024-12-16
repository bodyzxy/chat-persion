package com.example.service;

import com.example.component.BaseResponse;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/30 21:02
 */
public interface DatabaseService {
    Long createDatabase(Long id, String name);

    BaseResponse shareDatabase(Long id);

    BaseResponse delete(Long id);

    BaseResponse getShareDatabase();

    BaseResponse getUserDataBase(Long userId);

    BaseResponse getHotDatabase();
}
