package com.arnavgpt.valoride;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableJpaAuditing
public class ValoRideApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValoRideApplication.class, args);
    }
}