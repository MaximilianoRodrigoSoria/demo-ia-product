package com.ar.laboratory.demoiaproduct.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact();
        contact.setName("Laboratory Team");
        contact.setEmail("support@laboratory.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Demo IA Product API")
                .version("1.0.0")
                .description("API REST para la gestión de productos con inteligencia artificial")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info);
    }
}
