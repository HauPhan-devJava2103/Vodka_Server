package com.vn.vodka_server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsResponse {

    private long totalUsers;

    private long activeUsers;

    private long inactiveUsers;

    private long userNewToday;
}
