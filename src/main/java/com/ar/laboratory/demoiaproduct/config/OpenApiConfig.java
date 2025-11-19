package com.ar.laboratory.demoiaproduct.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort + contextPath);
        localServer.setDescription("Servidor local de desarrollo");

        Contact contact = new Contact();
        contact.setName("Laboratory Team");
        contact.setEmail("support@laboratory.com");
        contact.setUrl("https://laboratory.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Demo IA Product API")
                .version("1.0.0")
                .description("API REST para la gestión de productos con inteligencia artificial. " +
                        "Esta API proporciona endpoints para crear, leer, actualizar y eliminar productos.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
