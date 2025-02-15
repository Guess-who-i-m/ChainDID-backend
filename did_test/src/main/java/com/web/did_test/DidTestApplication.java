package com.web.did_test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.web.did_test.mapper")
@SpringBootApplication
public class DidTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DidTestApplication.class, args);
    }

}
