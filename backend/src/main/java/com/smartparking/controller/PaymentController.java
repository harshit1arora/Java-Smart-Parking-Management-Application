package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PaymentController — Member 2 requirement.
 *
 * POST /api/payment/initiate   — mock Razorpay order creation
 * POST /api/payment/verify     — mock signature verification
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * POST /api/payment/initiate
     * Body: { "booking_id": 1, "amount": 20.00, "method": "UPI" }
     *
     * Returns mock Razorpay order: { order_id, amount (paise), currency }
     */
    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiatePayment(
            @RequestBody Map<String, Object> body) {

        int bookingId = Integer.parseInt(body.get("booking_id").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String method = (String) body.getOrDefault("method", "UPI");

        Map<String, Object> orderData = paymentService.initiatePayment(bookingId, amount, method);
        return ResponseEntity.ok(ApiResponse.success(orderData, "Payment order created. Proceed to payment."));
    }

    /**
     * POST /api/payment/verify
     * Body: { "order_id": "...", "payment_id": "...", "signature": "..." }
     *
     * Verifies (mock) and updates payment + booking status.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyPayment(
            @RequestBody Map<String, String> body) {

        String orderId    = body.getOrDefault("razorpay_order_id", body.get("order_id"));
        String paymentId  = body.getOrDefault("razorpay_payment_id", body.get("payment_id"));
        String signature  = body.getOrDefault("razorpay_signature", body.get("signature"));

        Map<String, Object> result = paymentService.verifyPayment(orderId, paymentId, signature);
        return ResponseEntity.ok(ApiResponse.success(result, "Payment verified successfully!"));
    }
}
