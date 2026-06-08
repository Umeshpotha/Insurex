package com.insurex;

import com.insurex.model.Policy;
import com.insurex.repository.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InsureXApplication {

    public static void main(String[] args) {
        SpringApplication.run(InsureXApplication.class, args);
    }


}
