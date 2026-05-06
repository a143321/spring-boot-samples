package com.example.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// @RestController = 「この클래스が Web API のエンドポイントです」
@RestController
public class HelloController {

    // GET /hello にアクセスしたときに呼ばれるメソッド
    @GetMapping("/hello")
    public String hello(@RequestParam(defaultValue = "World") String name) {
        return "こんにちは、" + name + "さん！Spring Boot へようこそ！";
    }

    // GET / にアクセスしたときのトップページ
    @GetMapping("/")
    public String index() {
        return "✅ Spring Boot が動いています！ → /hello にアクセスしてみてください";
    }
}
