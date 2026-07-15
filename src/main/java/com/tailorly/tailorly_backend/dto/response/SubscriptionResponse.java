package com.tailorly.tailorly_backend.dto.response;

import com.tailorly.tailorly_backend.model.enums.SubscriptionPlan;
import com.tailorly.tailorly_backend.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    private String id;

    private String userId;

    private SubscriptionPlan plan;

    private SubscriptionStatus status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private Long amount;

    private String currency;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean active;
}
