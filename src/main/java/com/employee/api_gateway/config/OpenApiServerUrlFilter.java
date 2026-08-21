package com.employee.api_gateway.config;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class OpenApiServerUrlFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    public OpenApiServerUrlFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // Apply only to Swagger/OpenAPI definitions
        if (!path.endsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();

        ServerHttpResponseDecorator decoratedResponse =
                new ServerHttpResponseDecorator(originalResponse) {

                    @Override
                    public Mono<Void> writeWith(
                            org.reactivestreams.Publisher<? extends DataBuffer> body) {

                        if (body instanceof Flux) {

                            Flux<? extends DataBuffer> flux =
                                    Flux.from(body);

                            return DataBufferUtils.join(flux)
                                    .flatMap(dataBuffer -> {

                                        byte[] bytes = new byte[
                                                dataBuffer.readableByteCount()
                                                ];

                                        dataBuffer.read(bytes);

                                        DataBufferUtils.release(dataBuffer);

                                        try {
                                            String json =
                                                    new String(
                                                            bytes,
                                                            StandardCharsets.UTF_8);

                                            JsonNode root =
                                                    objectMapper.readTree(json);

                                            if (root.has("servers")) {

                                                ArrayNode servers =
                                                        objectMapper.createArrayNode();

                                                ObjectNode server =
                                                        objectMapper.createObjectNode();

                                                server.put(
                                                        "url",
                                                        "http://localhost:8080");

                                                server.put(
                                                        "description",
                                                        "API Gateway");

                                                servers.add(server);

                                                ((ObjectNode) root)
                                                        .set("servers", servers);
                                            }

                                            byte[] modifiedBytes =
                                                    objectMapper
                                                            .writeValueAsBytes(root);

                                            originalResponse.getHeaders()
                                                    .setContentLength(
                                                            modifiedBytes.length);

                                            DataBuffer buffer =
                                                    originalResponse
                                                            .bufferFactory()
                                                            .wrap(modifiedBytes);

                                            return originalResponse
                                                    .writeWith(Mono.just(buffer));

                                        } catch (Exception e) {
                                            return Mono.error(e);
                                        }
                                    });
                        }

                        return super.writeWith(body);
                    }
                };

        return chain.filter(
                exchange.mutate()
                        .response(decoratedResponse)
                        .build());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
