package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ipn {
    private boolean sent;
    private MerchantIpn merchant;
}
