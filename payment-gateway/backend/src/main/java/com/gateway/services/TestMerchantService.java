package com.gateway.services;

import com.gateway.dto.response.TestMerchantResponse;
import com.gateway.exception.ApiException;
import com.gateway.exception.NotFoundException;
import com.gateway.models.Merchant;
import com.gateway.repositories.MerchantRepository;
import org.springframework.stereotype.Service;

@Service
public class TestMerchantService {

    private static final String TEST_API_KEY = "key_test_abc123";

    private final MerchantRepository merchantRepository;

    public TestMerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public TestMerchantResponse getTestMerchant() {

        Merchant merchant = merchantRepository
                .findByApiKey(TEST_API_KEY)
                .orElseThrow(() ->
                        new NotFoundException("Test merchant not found")
                );

        return new TestMerchantResponse(
                merchant.getId(),
                merchant.getEmail(),
                merchant.getApiKey(),
                true
        );
    }
}
