package com.originacion.contratos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Contratos y Pagarés")
                        .description("Microservicio para gestión de contratos y pagarés de préstamos automotrices")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@originacion.com"))
                        .license(new License()
                                .name("MIT License")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.originacion.com")
                                .description("Servidor de Producción")
                ));
    }
} 