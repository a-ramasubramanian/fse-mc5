package com.stackroute.gateway.config;

import com.stackroute.gateway.filter.JwtValidationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Bean Configuration for API Gateway.
 *  **TODO**
 * Add appropriate annotation to this Bean Configuration class
 */
@Configuration
public class GatewayBeanConfig {

    public static final String CUSTOMER_PROFILE_API_URL = "/api/v1/customers/*";
    public static final String PRODUCTS_SERVICE_API_URL = "/api/v1/products/*";
    public static final String ORDER_SERVICE_API_URL = "/api/v1/orders/*";

    /**
     * Below bean configuration registers the JWTValidationFilter to intercept the
     * requests to Product Service, Order Service and Customer profile api of User service
     */
    @Bean
    public FilterRegistrationBean<GenericFilterBean> jwtFilter() {
        FilterRegistrationBean<GenericFilterBean> filter = new FilterRegistrationBean<>();
        filter.setFilter(new JwtValidationFilter());
        filter.addUrlPatterns(CUSTOMER_PROFILE_API_URL,
                PRODUCTS_SERVICE_API_URL,
                ORDER_SERVICE_API_URL);
        return filter;
    }

    /**
     * Gateway Offloading:
     * All the microservices should allow cross origin requests. Instead of doing the configuration in individual microservices,
     * Configuration for allowing the cross origin requests can be offloaded to the API gateway
     * <p>
     * **TODO**
     * <p>
     * Configure a CorsFilter bean below to allow cross origin requests to microservices
     * - From all origins (*)
     * - For Http Methods - OPTIONS, GET, PUT, POST, DELETE
     * Add appropriate annotation to the method
     */

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedMethod(HttpMethod.OPTIONS);
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.DELETE);

        source.registerCorsConfiguration("*", config);
        return new CorsFilter(source);

    }
}