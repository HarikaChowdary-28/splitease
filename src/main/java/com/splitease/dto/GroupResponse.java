package com.splitease.dto;
import lombok.Setter;
import lombok.Getter;
import java.time.Instant;

@Getter
@Setter
public class GroupResponse {
    private long id;
    private String name;
    private Long createdById;
    private String createdByName;
    private Instant createdAt;
}
