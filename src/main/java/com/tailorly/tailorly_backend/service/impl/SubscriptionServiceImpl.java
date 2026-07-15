package com.tailorly.tailorly_backend.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.tailorly.tailorly_backend.config.RazorpayProperties;
import com.tailorly.tailorly_backend.dto.request.VerifyPaymentRequest;
import com.tailorly.tailorly_backend.dto.response.ApiResponse;
import com.tailorly.tailorly_backend.dto.response.CreateOrderResponse;
import com.tailorly.tailorly_backend.dto.response.SubscriptionResponse;
import com.tailorly.tailorly_backend.dto.response.UsageResponse;
import com.tailorly.tailorly_backend.exception.PaymentProcessingException;
import com.tailorly.tailorly_backend.exception.ResourceNotFoundException;
import com.tailorly.tailorly_backend.exception.SubscriptionAccessException;
import com.tailorly.tailorly_backend.model.Subscription;
import com.tailorly.tailorly_backend.model.Usage;
import com.tailorly.tailorly_backend.model.User;
import com.tailorly.tailorly_backend.model.enums.SubscriptionPlan;
import com.tailorly.tailorly_backend.model.enums.SubscriptionStatus;
import com.tailorly.tailorly_backend.repository.SubscriptionRepository;
import com.tailorly.tailorly_backend.repository.UsageRepository;
import com.tailorly.tailorly_backend.service.CurrentUserService;
import com.tailorly.tailorly_backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private static final int FREE_TAILORING_LIMIT = 3;
    private static final long MONTHLY_AMOUNT = 29900L;
    private static final String CURRENCY = "INR";
    private static final String FREE_LIMIT_MESSAGE =
            "Free limit exhausted. Please purchase a monthly subscription.";
    private static final String ATS_MESSAGE =
            "Premium subscription required to access ATS score.";

    private final RazorpayClient razorpayClient;
    private final RazorpayProperties razorpayProperties;
    private final CurrentUserService currentUserService;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageRepository usageRepository;

    @Override
    public ApiResponse<CreateOrderResponse> createOrder() {
        User user = currentUserService.getCurrentUser();

        Subscription subscription = Subscription.builder()
                .userId(user.getId())
                .plan(SubscriptionPlan.MONTHLY)
                .status(SubscriptionStatus.PENDING)
                .amount(MONTHLY_AMOUNT)
                .currency(CURRENCY)
                .build();

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        try {
            JSONObject orderRequest = new JSONObject()
                    .put("amount", MONTHLY_AMOUNT)
                    .put("currency", CURRENCY)
                    .put("receipt", savedSubscription.getId());

            Order order = razorpayClient.orders.create(orderRequest);
            String razorpayOrderId = order.get("id").toString();

            savedSubscription.setRazorpayOrderId(razorpayOrderId);
            savedSubscription = subscriptionRepository.save(savedSubscription);

            CreateOrderResponse response = CreateOrderResponse.builder()
                    .subscriptionId(savedSubscription.getId())
                    .razorpayOrderId(razorpayOrderId)
                    .keyId(razorpayProperties.getKeyId())
                    .amount(savedSubscription.getAmount())
                    .currency(savedSubscription.getCurrency())
                    .plan(savedSubscription.getPlan())
                    .build();

            return ApiResponse.<CreateOrderResponse>builder()
                    .success(true)
                    .message("Razorpay order created successfully")
                    .data(response)
                    .build();
        } catch (RazorpayException ex) {
            ex.printStackTrace();

            System.out.println("Razorpay Error: " + ex.getMessage());

            subscriptionRepository.delete(savedSubscription);

            throw new PaymentProcessingException(
                    "Failed to create Razorpay order",
                    ex
            );
        } catch (RuntimeException ex) {
            subscriptionRepository.delete(savedSubscription);
            throw ex;
        }
    }

    @Override
    public ApiResponse<SubscriptionResponse> verifyPayment(VerifyPaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment verification request is required");
        }

        User user = currentUserService.getCurrentUser();
        Subscription subscription = subscriptionRepository.findByRazorpayOrderId(
                        request.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        if (!user.getId().equals(subscription.getUserId())) {
            throw new ResourceNotFoundException("Subscription not found");
        }

        verifySignature(request);

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE
                || !request.getRazorpayPaymentId().equals(subscription.getRazorpayPaymentId())) {

            LocalDateTime now = LocalDateTime.now();
            subscription.setRazorpayPaymentId(request.getRazorpayPaymentId());
            subscription.setRazorpaySignature(request.getRazorpaySignature());
            subscription.setStatus(SubscriptionStatus.ACTIVE);
            subscription.setStartDate(now);
            subscription.setEndDate(now.plusMonths(1));
            subscription = subscriptionRepository.save(subscription);
        }

        return ApiResponse.<SubscriptionResponse>builder()
                .success(true)
                .message("Subscription activated successfully")
                .data(toResponse(subscription))
                .build();
    }

    @Override
    public ApiResponse<SubscriptionResponse> getSubscription() {
        User user = currentUserService.getCurrentUser();
        Subscription subscription = getDisplaySubscription(user.getId()).orElse(null);

        return ApiResponse.<SubscriptionResponse>builder()
                .success(true)
                .message("Subscription fetched successfully")
                .data(subscription == null ? null : toResponse(subscription))
                .build();
    }

    @Override
    public ApiResponse<UsageResponse> getUsage() {
        User user = currentUserService.getCurrentUser();
        Usage usage = getOrCreateUsage(user.getId());
        boolean premiumActive = hasActiveSubscription(user.getId());
        int count = usage.getResumeTailoringCount() == null ? 0 : usage.getResumeTailoringCount();

        UsageResponse response = UsageResponse.builder()
                .userId(user.getId())
                .resumeTailoringCount(count)
                .freeLimit(FREE_TAILORING_LIMIT)
                .remainingFreeRequests(premiumActive ? null : Math.max(0, FREE_TAILORING_LIMIT - count))
                .premiumActive(premiumActive)
                .atsUnlocked(premiumActive)
                .lastUpdated(usage.getLastUpdated())
                .build();

        return ApiResponse.<UsageResponse>builder()
                .success(true)
                .message("Usage fetched successfully")
                .data(response)
                .build();
    }

    @Override
    public void enforceResumeTailoringAccess() {
        User user = currentUserService.getCurrentUser();

        if (hasActiveSubscription(user.getId())) {
            return;
        }

        Usage usage = getOrCreateUsage(user.getId());
        int count = usage.getResumeTailoringCount() == null ? 0 : usage.getResumeTailoringCount();

        if (count >= FREE_TAILORING_LIMIT) {
            throw new SubscriptionAccessException(FREE_LIMIT_MESSAGE);
        }
    }

    @Override
    public void recordSuccessfulResumeTailoring() {
        User user = currentUserService.getCurrentUser();
        Usage usage = getOrCreateUsage(user.getId());
        int count = usage.getResumeTailoringCount() == null ? 0 : usage.getResumeTailoringCount();

        usage.setResumeTailoringCount(count + 1);
        usage.setLastUpdated(LocalDateTime.now());
        usageRepository.save(usage);
    }

    @Override
    public void enforceAtsAccess() {
        User user = currentUserService.getCurrentUser();

        if (!hasActiveSubscription(user.getId())) {
            throw new SubscriptionAccessException(ATS_MESSAGE);
        }
    }

    private Optional<Subscription> getDisplaySubscription(String userId) {
        Optional<Subscription> activeSubscription = getActiveSubscription(userId);
        if (activeSubscription.isPresent()) {
            return activeSubscription;
        }

        return subscriptionRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .map(this::refreshSubscriptionStateIfExpired);
    }

    private Optional<Subscription> getActiveSubscription(String userId) {
        return subscriptionRepository.findTopByUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        SubscriptionStatus.ACTIVE)
                .map(this::refreshSubscriptionStateIfExpired)
                .flatMap(subscription -> subscription.getStatus() == SubscriptionStatus.ACTIVE
                        ? Optional.of(subscription)
                        : Optional.empty());
    }

    private boolean hasActiveSubscription(String userId) {
        return getActiveSubscription(userId).isPresent();
    }

    private Subscription refreshSubscriptionStateIfExpired(Subscription subscription) {
        if (subscription == null
                || subscription.getStatus() != SubscriptionStatus.ACTIVE
                || subscription.getEndDate() == null
                || subscription.getEndDate().isAfter(LocalDateTime.now())) {
            return subscription;
        }

        subscription.setStatus(SubscriptionStatus.EXPIRED);
        return subscriptionRepository.save(subscription);
    }

    private Usage getOrCreateUsage(String userId) {
        return usageRepository.findByUserId(userId)
                .orElseGet(() -> usageRepository.save(
                        Usage.builder()
                                .userId(userId)
                                .resumeTailoringCount(0)
                                .lastUpdated(LocalDateTime.now())
                                .build()
                ));
    }

    private void verifySignature(VerifyPaymentRequest request) {
        try {
            JSONObject attributes = new JSONObject()
                    .put("razorpay_order_id", request.getRazorpayOrderId())
                    .put("razorpay_payment_id", request.getRazorpayPaymentId())
                    .put("razorpay_signature", request.getRazorpaySignature());

            boolean valid = Utils.verifyPaymentSignature(
                    attributes,
                    razorpayProperties.getKeySecret()
            );

            if (!valid) {
                throw new IllegalArgumentException("Invalid Razorpay signature");
            }
        } catch (RazorpayException ex) {
            throw new IllegalArgumentException("Invalid Razorpay signature", ex);
        }
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        boolean active = subscription.getStatus() == SubscriptionStatus.ACTIVE
                && subscription.getEndDate() != null
                && subscription.getEndDate().isAfter(LocalDateTime.now());

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .userId(subscription.getUserId())
                .plan(subscription.getPlan())
                .status(subscription.getStatus())
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .razorpayOrderId(subscription.getRazorpayOrderId())
                .razorpayPaymentId(subscription.getRazorpayPaymentId())
                .amount(subscription.getAmount())
                .currency(subscription.getCurrency())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .active(active)
                .build();
    }
}
