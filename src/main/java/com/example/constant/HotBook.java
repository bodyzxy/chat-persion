package com.example.constant;

import lombok.Data;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/12 11:52
 */
@Data
public class HotBook {

    public String prompt = "给我找30条关于学习自然语言处理的书籍并以下面的json格式返回,我要求你返回时的text的json字段像下面这个格式一样，前后没有任何多余字段"+
            "[" +
            "{\n" +
            "\"id\": ,\n" +
            "\"title\": \"\",\n" +
            "\"description\": \"\",\n" +
            "\"url\":\"\"\n" +
            "}," +
            "{\n" +
            "\"id\": ,\n" +
            "\"title\": \"\",\n" +
            "\"description\": \"\",\n" +
            "\"url\":\"\"\n" +
            "}"+
            "]"+
            "将地址写到url这里,同时你必须使用中文进行回答，若项目为其他语言将其翻译后写入json中";
}
