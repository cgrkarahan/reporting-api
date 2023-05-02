package com.rpdpymnt.reportingapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionReportData {

    private Integer count;

    private Integer total;

    private String currency;
}
