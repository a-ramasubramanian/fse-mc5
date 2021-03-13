package com.stackroute.orderservice.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;


/**
 * Configuration for API Documentation using Swagger
 */
@Configuration
@EnableSwagger2
public class OrderServiceConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public Docket orderApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.stackroute.orderservice.controller"))
                .paths(PathSelectors.regex("/api/v1/orders.*"))
                .build()
                .apiInfo(metadata());
    }

    private ApiInfo metadata() {
        return new ApiInfo("Order Service API", "API for creating and getting orders", "1.0", "", new Contact("Ghouse", null, null),
                "gpl", null, new ArrayList<>());
    }
}
