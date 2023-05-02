package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantInfo {
    private int id;
    private String name;
    private boolean allowPartialRefund;
    private boolean allowPartialCapture;
}
