package com.aimms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AiMmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiMmsApplication.class, args);
    }
}
