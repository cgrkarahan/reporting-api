package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfo {
    private String billingFirstName;
    private String billingLastName;
    private Object issueNumber;
    private String email;
    private String billingCompany;
    private String billingCity;
    private String updated_at;
    private String created_at;
    private int id;
}
