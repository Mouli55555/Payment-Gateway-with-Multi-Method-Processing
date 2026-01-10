package com.gateway.config;

import com.gateway.repositories.MerchantRepository;
import com.gateway.security.ApiKeyAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final MerchantRepository merchantRepository;

    public SecurityConfig(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/health",
                                "/api/v1/health",
                                "/api/v1/test/**",
                                "/api/v1/orders/public/**",
                                "/api/v1/payments/public/**"
                        ).permitAll()


                        // 🔐 Everything else requires merchant API key
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new ApiKeyAuthFilter(merchantRepository),
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}
