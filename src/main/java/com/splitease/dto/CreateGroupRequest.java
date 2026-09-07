package com.splitease.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long createdBy; //the id of user who created the group
}
