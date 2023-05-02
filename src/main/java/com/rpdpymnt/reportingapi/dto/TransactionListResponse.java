package com.rpdpymnt.reportingapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListResponse {


    private Integer per_page;
    private Integer current_page;
    private String next_page_url;
    private String prev_page_url;
    private Integer from;
    private Integer to;
    private List<TransactionInfo> data;
}
