package ru.lab1.dealership.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Dealership API",
            version = "1.0",
            description = "REST API for the dealership laboratory work"))
public class OpenApiConfig {}
