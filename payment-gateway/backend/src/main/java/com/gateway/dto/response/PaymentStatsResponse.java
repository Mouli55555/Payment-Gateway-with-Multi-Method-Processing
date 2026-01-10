package com.gateway.dto.response;

public class PaymentStatsResponse {

    private long total_transactions;
    private long total_amount;
    private double success_rate;

    public PaymentStatsResponse(
            long totalTransactions,
            long totalAmount,
            double successRate
    ) {
        this.total_transactions = totalTransactions;
        this.total_amount = totalAmount;
        this.success_rate = successRate;
    }

    public long getTotal_transactions() {
        return total_transactions;
    }

    public long getTotal_amount() {
        return total_amount;
    }

    public double getSuccess_rate() {
        return success_rate;
    }
}
