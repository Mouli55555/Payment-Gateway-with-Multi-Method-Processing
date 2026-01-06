package com.gateway.controllers;

import com.gateway.dto.response.TestMerchantResponse;
import com.gateway.services.TestMerchantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestMerchantController {

    private final TestMerchantService testMerchantService;

    public TestMerchantController(TestMerchantService testMerchantService) {
        this.testMerchantService = testMerchantService;
    }

    @GetMapping("/merchant")
    public TestMerchantResponse getTestMerchant() {
        return testMerchantService.getTestMerchant();
    }
}
