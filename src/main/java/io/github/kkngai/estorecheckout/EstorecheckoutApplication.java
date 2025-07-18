package io.github.kkngai.estorecheckout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("io.github.kkngai.estorecheckout.model")
public class EstorecheckoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(EstorecheckoutApplication.class, args);
    }

} 