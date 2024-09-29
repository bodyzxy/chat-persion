package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;

@SpringBootTest
class ChatPersonApplicationTests {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/chat-person-test";
        String user = "postgres";
        String password = "bodyzxy";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connection successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void contextLoads() {

    }

}
