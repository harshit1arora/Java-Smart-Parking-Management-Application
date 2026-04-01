package com.smartparking.service;

import com.smartparking.exception.PaymentException;
import com.smartparking.exception.ResourceNotFoundException;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import com.smartparking.model.Payment;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * PaymentService — Member 2 requirement.
 *
 * Integrates with Razorpay for order creation and payment verification.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Initiate a payment — creates a Razorpay order.
     */
    @Transactional
    public Map<String, Object> initiatePayment(int bookingId, BigDecimal amount, String method) {
        // Validate booking exists
        bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));

        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); // paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "txn_" + bookingId);

            Order order = razorpay.orders.create(orderRequest);
            String orderId = order.get("id");

            // Save pending payment
            paymentRepository.save(bookingId, amount, method, orderId);

            Map<String, Object> response = new HashMap<>();
            response.put("order_id", orderId);
            response.put("amount", amount.multiply(BigDecimal.valueOf(100)).intValue()); 
            response.put("currency", "INR");
            response.put("key", razorpayKeyId);

            log.info("[PaymentService] Razorpay Order created: orderId={}", orderId);
            return response;

        } catch (RazorpayException e) {
            log.error("Razorpay error: ", e);
            throw new PaymentException("Failed to initiate payment: " + e.getMessage());
        }
    }

    /**
     * Verify payment — validates Razorpay signature and updates status.
     */
    @Transactional
    public Map<String, Object> verifyPayment(String orderId, String paymentId, String signature) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if ("success".equals(payment.getStatus())) {
            throw new PaymentException("Payment already verified for order: " + orderId);
        }

        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            boolean verified = Utils.verifyPaymentSignature(options, razorpayKeySecret);

            if (!verified) {
                paymentRepository.markFailed(orderId);
                throw new PaymentException("Payment verification failed. Invalid signature.");
            }

            // Mark payment success
            paymentRepository.markSuccess(orderId, paymentId);

            // Update booking to 'active' after payment confirmed
            bookingRepository.updateStatus(payment.getBookingId(), "active");

            log.info("[PaymentService] Razorpay Payment verified: orderId={}, txnId={}", orderId, paymentId);

            Map<String, Object> response = new HashMap<>();
            response.put("verified", true);
            response.put("transaction_id", paymentId);
            response.put("booking_id", payment.getBookingId());
            response.put("amount", payment.getAmount());
            response.put("status", "success");

            return response;

        } catch (RazorpayException e) {
            paymentRepository.markFailed(orderId);
            throw new PaymentException("Error verifying payment signature: " + e.getMessage());
        }
    }
}
