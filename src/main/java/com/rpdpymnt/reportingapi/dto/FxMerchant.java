package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FxMerchant {
    private int originalAmount;
    private String originalCurrency;
    private int convertedAmount;
    private String convertedCurrency;
}
