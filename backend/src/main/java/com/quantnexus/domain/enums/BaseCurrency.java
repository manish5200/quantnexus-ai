package com.quantnexus.domain.enums;

import lombok.Getter;

@Getter
public enum BaseCurrency {
    INR("₹"),
    USD("$"),
    EUR("€");

    private final String symbol;

    BaseCurrency(String symbol) {
        this.symbol = symbol;
    }
}