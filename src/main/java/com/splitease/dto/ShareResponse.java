package com.splitease.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShareResponse {
    private Long userId;
    private String userName;
    private BigDecimal shareAmount;
}
