package com.sms.smsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class SmsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsDemoApplication.class, args);
    }

}
