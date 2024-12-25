package com.bootlabs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@OpenAPIDefinition(info = @Info(
        title = "Spring WebFlux tfa",
        version = "1.0",
        description = "swagger documentation using open api."
))
@EnableWebFlux
@SpringBootApplication
public class SpringWebfluxTfaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxTfaApplication.class, args);
    }

}
