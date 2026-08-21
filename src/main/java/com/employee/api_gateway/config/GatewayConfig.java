package com.employee.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        return builder.routes()

                // =========================
                // AUTH SERVICE
                // =========================
                .route("auth-service", r -> r
                        .path("/api/auth/**")
                        .uri("http://localhost:8082"))

                // =========================
                // EMPLOYEE SERVICE
                // =========================
                .route("employee-service", r -> r
                        .path("/api/employees/**")
                        .uri("lb://EMPLOYEE-SERVICE"))

                .route("user-api-docs", r -> r
                        .path("/user-service/v3/api-docs")
                        .filters(f -> f
                                .rewritePath(
                                        "/user-service/v3/api-docs",
                                        "/v3/api-docs"))
                        .uri("lb://USER-SERVICE"))

                // =========================
                // USER SERVICE
                // =========================
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .uri("lb://USER-SERVICE"))

                // =========================
                // EMPLOYEE SWAGGER
                // =========================
                .route("employee-api-docs", r -> r
                        .path("/employee-service/v3/api-docs")
                        .filters(f -> f
                                .rewritePath(
                                        "/employee-service/v3/api-docs",
                                        "/v3/api-docs"))
                        .uri("lb://EMPLOYEE-SERVICE"))

                // =========================
                // USER SWAGGER
                // =========================
//                .route("user-api-docs", r -> r
//                        .path("/user-service/v3/api-docs")
//                        .filters(f -> f
//                                .rewritePath(
//                                        "/user-service/v3/api-docs",
//                                        "/v3/api-docs")
//                                .removeRequestHeader("Host"))
//                        .uri("lb://USER-SERVICE"))
//                .route("user-api-docs", r -> r
//                        .path("/user-service/v3/api-docs")
//                        .filters(f -> f
//                                .rewritePath(
//                                        "/user-service/v3/api-docs",
//                                        "/v3/api-docs"))
//                        .uri("http://localhost:8083"))

                // =========================
                // AUTH SWAGGER
                // =========================
                .route("auth-api-docs", r -> r
                        .path("/auth-service/v3/api-docs")
                        .filters(f -> f
                                .rewritePath(
                                        "/auth-service/v3/api-docs",
                                        "/v3/api-docs"))
                        .uri("http://localhost:8082"))

                .build();
    }
}


