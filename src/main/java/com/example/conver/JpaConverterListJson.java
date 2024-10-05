package com.example.conver;

import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import com.alibaba.fastjson2.JSON;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/5 18:32
 * List对象和Json字符串互转，用于属性与表字段的映射
 */
public class JpaConverterListJson implements AttributeConverter<Object, String> {
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null) {
            attribute = new ArrayList();
        }
        return JSON.toJSONString(attribute);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return JSON.parseArray(dbData);
    }
}
