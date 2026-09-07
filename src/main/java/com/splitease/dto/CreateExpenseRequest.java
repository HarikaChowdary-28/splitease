package com.splitease.dto;

import com.splitease.model.SplitType;
import jakarta.validation.constraints.*;
import lombok.Setter;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CreateExpenseRequest {
    @NotNull
    private long paidBy;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String description;

    @NotNull
    private SplitType splitType;

    @NotEmpty
    private List<Long> participantIds; //user ids to split among people
}
