package com.splitease.dto;

import com.splitease.model.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class AddMemberRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Role role;
}
