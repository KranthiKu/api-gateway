//package com.employee.api_gateway.config;
//
//
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class OpenApiGatewayFilter
//        extends AbstractGatewayFilterFactory<OpenApiGatewayFilter.Config> {
//
//    public OpenApiGatewayFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//
//        return (exchange, chain) ->
//                chain.filter(exchange)
//                        .then(Mono.defer(() -> {
//
//                            if (exchange.getResponse()
//                                    .getHeaders()
//                                    .getContentType() != null
//                                    && exchange.getResponse()
//                                    .getHeaders()
//                                    .getContentType()
//                                    .includes(MediaType.APPLICATION_JSON)) {
//
//                                return Mono.empty();
//                            }
//
//                            return Mono.empty();
//                        }));
//    }
//
//    public static class Config {
//    }
//}