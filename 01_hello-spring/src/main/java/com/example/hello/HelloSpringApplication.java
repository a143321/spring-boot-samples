package com.example.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication = 「これが Spring Boot アプリの起点です」という宣言
@SpringBootApplication
public class HelloSpringApplication {

    // Java アプリのエントリーポイント（他言語の main() と同じ）
    public static void main(String[] args) {
        SpringApplication.run(HelloSpringApplication.class, args);
    }
}
