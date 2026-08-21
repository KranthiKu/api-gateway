package com.employee.api_gateway.security;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;

import reactor.core.publisher.Mono;

@Component
@Order(-1)
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

        System.out.println(
                ">>> JwtAuthenticationFilter CREATED");
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        System.out.println(
                ">>> Gateway Path: " + path);

        // =====================================
        // PUBLIC AUTH APIs
        // =====================================

        if (path.startsWith("/api/auth/")) {

            System.out.println(
                    ">>> AUTH API - BYPASS JWT");

            return chain.filter(exchange);
        }

        // =====================================
        // PUBLIC SWAGGER APIs
        // =====================================

        if (path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/employee-service/v3/api-docs")
                || path.startsWith("/user-service/v3/api-docs")
                || path.startsWith("/auth-service/v3/api-docs")) {

            System.out.println(
                    ">>> SWAGGER API - BYPASS JWT");

            return chain.filter(exchange);
        }

        // =====================================
        // GET AUTHORIZATION HEADER
        // =====================================

        String authorizationHeader =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst(
                                HttpHeaders.AUTHORIZATION);

        // =====================================
        // JWT NOT PRESENT
        // =====================================

        if (authorizationHeader == null
                || !authorizationHeader
                .startsWith("Bearer ")) {

            System.out.println(
                    ">>> JWT NOT PRESENT - RETURN 401");

            exchange.getResponse()
                    .setStatusCode(
                            HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }

        // =====================================
        // EXTRACT TOKEN
        // =====================================

        String token =
                authorizationHeader.substring(7);

        System.out.println(
                ">>> JWT FOUND");

        // =====================================
        // VALIDATE TOKEN
        // =====================================

        if (!jwtUtil.isTokenValid(token)) {

            System.out.println(
                    ">>> INVALID JWT - RETURN 401");

            exchange.getResponse()
                    .setStatusCode(
                            HttpStatus.UNAUTHORIZED);

            return exchange.getResponse()
                    .setComplete();
        }

        // =====================================
        // TOKEN VALID
        // =====================================

        System.out.println(
                ">>> VALID JWT - REQUEST ALLOWED");

        return chain.filter(exchange);
    }
}