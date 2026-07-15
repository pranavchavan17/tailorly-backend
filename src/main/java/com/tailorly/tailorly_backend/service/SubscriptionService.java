package com.tailorly.tailorly_backend.service;

import com.tailorly.tailorly_backend.dto.request.VerifyPaymentRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.CreateOrderResponse;
import com.tailorly.tailorly_backend.dto.response.SubscriptionResponse;
import com.tailorly.tailorly_backend.dto.response.UsageResponse;

public interface SubscriptionService {

    ApiResponse<CreateOrderResponse> createOrder();

    ApiResponse<SubscriptionResponse> verifyPayment(VerifyPaymentRequest request);

    ApiResponse<SubscriptionResponse> getSubscription();

    ApiResponse<UsageResponse> getUsage();

    void enforceResumeTailoringAccess();

    void recordSuccessfulResumeTailoring();

    void enforceAtsAccess();
}
