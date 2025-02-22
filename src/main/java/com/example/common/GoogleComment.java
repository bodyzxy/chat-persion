package com.example.common;

import com.example.constant.WebConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


/**
 * 联网搜索
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2025/2/16 19:53
 */
@Component
public class GoogleComment {

    public String search(String query) throws IOException {
        WebConstant webConstant = new WebConstant();
        //URL编程搜索关键字
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s?key=%s&cx=%s&q=%s", webConstant.SEARCH_URL, webConstant.API_KEY, webConstant.CX, encodedQuery);
        List<String> results = new ArrayList<>();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getEntity().getContent());

                JsonNode items = root.path("items");
                StringBuilder jsonResult = new StringBuilder("[");
                int id = 1;

                for (JsonNode item : items) {
                    String title = item.path("title").asText();
                    String snippet = item.path("snippet").asText();
                    String urlResult = item.path("link").asText();

                    jsonResult.append(String.format(
                            "{ \"id\": %d, \"title\": \"%s\", \"description\": \"%s\", \"url\": \"%s\" },",
                            id++, title, snippet, urlResult
                    ));
                }

                if (jsonResult.length() > 1) {
                    jsonResult.setLength(jsonResult.length() - 1); // 移除最后一个逗号
                }
                jsonResult.append("]");

                return jsonResult.toString(); // 返回符合 JSON 格式的字符串
            }
        }
    }
}
