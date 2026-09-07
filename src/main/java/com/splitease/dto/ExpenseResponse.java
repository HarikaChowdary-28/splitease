package com.splitease.dto;

import com.splitease.model.SplitType;
import lombok.Setter;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ExpenseResponse {
    private Long id;
    private Long groupId;
    private Long paidById;
    private String paidByName;
    private BigDecimal amount;
    private String description;
    private SplitType splitType;
    private List<ShareResponse> shares; //each users share in tht group
    private Instant createdAt;
}
