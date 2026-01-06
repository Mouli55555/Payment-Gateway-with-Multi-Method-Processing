package com.gateway.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardDetails {

    private String number;

    @JsonProperty("expiry_month")
    private String expiryMonth;

    @JsonProperty("expiry_year")
    private String expiryYear;

    private String cvv;

    @JsonProperty("holder_name")
    private String holderName;

    public String getNumber() {
        return number;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String getCvv() {
        return cvv;
    }

    public String getHolderName() {
        return holderName;
    }
}
