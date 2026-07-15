package com.tailorly.tailorly_backend.model;

import com.tailorly.tailorly_backend.model.enums.SubscriptionPlan;
import com.tailorly.tailorly_backend.model.enums.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    private String id;

    private String userId;

    @Builder.Default
    private SubscriptionPlan plan = SubscriptionPlan.MONTHLY;

    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private Long amount;

    private String currency;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
