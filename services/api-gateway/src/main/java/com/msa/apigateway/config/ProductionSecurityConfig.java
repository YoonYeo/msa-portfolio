package com.msa.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@Profile("prd")
public class ProductionSecurityConfig {


    @Bean
    public SecurityWebFilterChain productionSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchange -> exchange
                // 헬스체크만 공개
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/actuator/**").authenticated()
                .anyExchange().authenticated()
            )
            .build();
    }
}
