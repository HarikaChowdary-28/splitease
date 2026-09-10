package com.splitease.dto;

import com.splitease.model.Role;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class MembershipResponse {
    private Long id;
    private Long groupId;
    private Long userId;
    private String userName;
    private Role role;
    private Instant joinedAt;
}
