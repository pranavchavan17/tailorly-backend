package com.tailorly.tailorly_backend.controller;

import com.tailorly.tailorly_backend.dto.request.VerifyPaymentRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.CreateOrderResponse;
import com.tailorly.tailorly_backend.dto.response.SubscriptionResponse;
import com.tailorly.tailorly_backend.dto.response.UsageResponse;
import com.tailorly.tailorly_backend.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/create-order")
    public ApiResponse<CreateOrderResponse> createOrder() {
        return subscriptionService.createOrder();
    }

    @PostMapping("/verify")
    public ApiResponse<SubscriptionResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        return subscriptionService.verifyPayment(request);
    }

    @GetMapping("/subscription")
    public ApiResponse<SubscriptionResponse> getSubscription() {
        return subscriptionService.getSubscription();
    }

    @GetMapping("/usage")
    public ApiResponse<UsageResponse> getUsage() {
        return subscriptionService.getUsage();
    }
}
