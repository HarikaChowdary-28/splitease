package com.splitease.dto;

import lombok.Setter;
import lombok.Getter;
import java.time.Instant;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Instant createdAt;
}
