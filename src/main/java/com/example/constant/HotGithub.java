package com.example.constant;

import lombok.Data;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/11 19:57
 */
@Data
public class HotGithub {
    public String prompt = "自然语言处理 热门Github项目 20条";
    public String hint = "请严格按照以下 JSON 格式整理搜索结果，并只返回 JSON，不要添加额外的文字：\n" +
            "[\n" +
            "{ \"id\": 1, \"title\": \"\", \"description\": \"\", \"url\": \"\" },\n" +
            "{ \"id\": 2, \"title\": \"\", \"description\": \"\", \"url\": \"\" }\n" +
            "]\n" +
            "请确保：\n" +
            "1. **id 递增**，从 1 开始。\n" +
            "2. **title** 使用项目名称，**description** 提取项目内容摘要300字内即可。\n" +
            "3. **url** 必须是可访问的项目的链接。\n" +
            "4. 只返回 JSON，不要添加任何多余的解释。\n" +
            "以下是搜索结果，请按照 JSON 格式整理：\n";
}
