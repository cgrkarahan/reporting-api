package com.rpdpymnt.reportingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Operation {
    DIRECT("DIRECT"),
    REFUND("REFUND"),
    _3D("3D"),
    _3DAUTH("3DAUTH"),
    STORED("STORED");

    @Getter
    private String description;
}
