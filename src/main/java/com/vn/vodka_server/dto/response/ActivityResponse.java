package com.vn.vodka_server.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityResponse {
    private long id;
    private String actorName;
    private String actorAvatar;
    private String entityType;
    private String targetName;
    private String createdAt;
    private String updatedAt;
}
