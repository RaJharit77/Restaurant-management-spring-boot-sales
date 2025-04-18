package com.rajharit.rajharitspringpointvente.springBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan({"com.rajharit.rajharitspringpointvente.springBootApplication", "helloWorld", "com.rajharit.rajharitspringpointvente"})
public class RaJharitSpringPointVenteApplication {

    public static void main(String[] args) {
        SpringApplication.run(RaJharitSpringPointVenteApplication.class, args);
    }

}
