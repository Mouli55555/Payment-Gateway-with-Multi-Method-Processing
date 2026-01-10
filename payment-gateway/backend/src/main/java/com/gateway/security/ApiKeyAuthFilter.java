package com.gateway.security;

import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final MerchantRepository merchantRepository;

    public ApiKeyAuthFilter(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    // ✅ VERY IMPORTANT FIX
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.startsWith("/health")
                || path.startsWith("/api/v1/test")
                || path.startsWith("/api/v1/orders/public")
                || path.startsWith("/api/v1/payments/public");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String apiKey = request.getHeader("X-Api-Key");
        String apiSecret = request.getHeader("X-Api-Secret");

        if (apiKey == null || apiSecret == null) {
            sendAuthError(response);
            return;
        }

        Optional<Merchant> merchantOpt =
                merchantRepository.findByApiKeyAndApiSecretAndIsActiveTrue(
                        apiKey, apiSecret
                );

        if (merchantOpt.isEmpty()) {
            sendAuthError(response);
            return;
        }

        Merchant merchant = merchantOpt.get();

        try {
            MerchantContext.set(merchant);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            merchant.getId(),
                            null,
                            Collections.emptyList()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } finally {
            MerchantContext.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private void sendAuthError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("""
            {
              "error": {
                "code": "AUTHENTICATION_ERROR",
                "description": "Invalid API credentials"
              }
            }
        """);
    }
}
