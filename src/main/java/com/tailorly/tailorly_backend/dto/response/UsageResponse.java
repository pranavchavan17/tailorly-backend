package com.tailorly.tailorly_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageResponse {

    private String userId;

    private Integer resumeTailoringCount;

    private Integer freeLimit;

    private Integer remainingFreeRequests;

    private boolean premiumActive;

    private boolean atsUnlocked;

    private LocalDateTime lastUpdated;
}
