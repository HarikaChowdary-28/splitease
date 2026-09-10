package com.splitease.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SplitInput {
    @NotNull
    private Long userId;

    private BigDecimal value;
}
