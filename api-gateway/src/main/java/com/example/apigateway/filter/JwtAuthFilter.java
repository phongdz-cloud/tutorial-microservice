package com.example.apigateway.filter;

import com.cursor.common.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final String BEARER = "Bearer ";
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // Allow unauthenticated access to /auth and swagger
        String path = request.getURI().getPath();

        if (path.startsWith("/auth")
                || path.startsWith("/swagger")
                || path.startsWith("/user-service/v3/api-docs"))
            return chain.filter(exchange);

        String auth = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(auth == null || !auth.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = auth.substring(BEARER.length());

        try {
            Claims claims = jwtService.parseAndValidate(token);
            String userId = claims.getSubject();
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .header("X-Roles", String.join(",", roles == null ? List.of() : roles))
                    .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        }catch (JwtException e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
