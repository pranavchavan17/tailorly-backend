package com.tailorly.tailorly_backend.dto.response;

import com.tailorly.tailorly_backend.model.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {

    private String subscriptionId;

    private String razorpayOrderId;

    private String keyId;

    private Long amount;

    private String currency;

    private SubscriptionPlan plan;
}
