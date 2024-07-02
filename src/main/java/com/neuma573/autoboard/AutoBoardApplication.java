package com.neuma573.autoboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class AutoBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoBoardApplication.class, args);
    }

}