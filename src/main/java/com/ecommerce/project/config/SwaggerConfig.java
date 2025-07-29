package com.ecommerce.project.config;




import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        SecurityScheme bearerScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Bearer token");

        SecurityRequirement bearerRequirement = new SecurityRequirement()
                .addList("bearerAuth");
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot eCommerce API")
                        .version("1.0")
                        .description("This is a spring boot project for eCommerce")
                        .license(new License().url("http://alabelewe.com"))
                        .contact(new Contact()
                                .name("Osein Ridwan")
                                .email("oseinridwan5@gmail.com")
                                .url("http://alabelewe.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation")
                        .url("http://alabelewe.com"))
                .components(new Components()
                .addSecuritySchemes("bearerAuth", bearerScheme))
                .addSecurityItem(bearerRequirement);
    }

}
