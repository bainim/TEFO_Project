package com.talan.empreintecarbone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages="com.talan.empreintecarbone") //notice this line
@SpringBootApplication
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class TefoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TefoApplication.class, args);
    }
}