package com.neuma573.autoboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableJpaAuditing
@EnableRedisRepositories
@SpringBootApplication
public class AutoBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoBoardApplication.class, args);
    }

}
