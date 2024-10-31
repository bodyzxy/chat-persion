package com.example.common;

/**
 * @author bodyzxy
 * @github https://github.com/bodyzxy
 * @date 2024/10/31 21:26
 */
public class ApplicationConstant {
    public final static String SYSTEM_PROMPT = """
        Use the information from the DOCUMENTS section to provide accurate answers but act as if you knew this information innately.
        If unsure, simply state that you don't know.
        Another thing you need to note is that your reply must be in Chinese!
        DOCUMENTS:
            {documents}    
        """;
}
