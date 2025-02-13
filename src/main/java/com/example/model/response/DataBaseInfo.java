package com.example.model.response;

import lombok.Data;

import java.util.Date;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/6 18:51
 */
@Data
public class DataBaseInfo {
    private Long id;
    private String name;
    private String title;
    private Date time;
}
