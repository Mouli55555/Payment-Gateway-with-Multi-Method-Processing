package com.gateway.security;

import com.gateway.models.Merchant;

public class MerchantContext {

    private static final ThreadLocal<Merchant> CURRENT = new ThreadLocal<>();

    public static void set(Merchant merchant) {
        CURRENT.set(merchant);
    }

    public static Merchant get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
