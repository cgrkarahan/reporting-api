package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private CustomerInfo customerInfo;
    private String updated_at;
    private String created_at;
    private Fx fx;
    private Acquirer acquirer;
    private Transaction transaction;
    private boolean refundable;
    private MerchantInfo merchant;
    private Ipn ipn;
}
