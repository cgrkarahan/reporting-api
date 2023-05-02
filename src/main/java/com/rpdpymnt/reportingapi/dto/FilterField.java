package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FilterField {
    TRANSACTION_UUID("Transaction   UUID"),
    CUSTOMER_EMAIL("Customer   Email"),
    REFERENCE_NO("Reference   No"),
    CUSTOM_DATA("Custom   Data"),
    CARD_PAN("Card   PAN");


    @Getter
    private String description;
}
