package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.model.Booking;
import com.smartparking.service.BookingService;
import com.smartparking.util.BillingUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BookingController — Member 1 requirement.
 *
 * POST /api/bookSlot
 * POST /api/releaseSlot
 * GET  /api/bookings/{user_id}
 */
@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final BillingUtil    billingUtil;

    public BookingController(BookingService bookingService, BillingUtil billingUtil) {
        this.bookingService = bookingService;
        this.billingUtil    = billingUtil;
    }

    /**
     * POST /api/bookSlot
     * Body: { "user_id": 1, "slot_id": 4, "duration_minutes": 60 }
     *
     * 1. Validates slot is free
     * 2. Marks slot as 'reserved'
     * 3. Creates booking record
     * 4. Returns booking_id + estimated_amount
     */
    @PostMapping("/bookSlot")
    public ResponseEntity<ApiResponse<Map<String, Object>>> bookSlot(
            @RequestBody Map<String, Object> body) {

        int userId          = Integer.parseInt(body.get("user_id").toString());
        int slotId          = Integer.parseInt(body.get("slot_id").toString());
        int durationMinutes = Integer.parseInt(body.getOrDefault("duration_minutes", 60).toString());

        int bookingId = bookingService.bookSlot(userId, slotId, durationMinutes);
        BigDecimal estimatedAmount = billingUtil.estimateCost(durationMinutes);

        Map<String, Object> data = new HashMap<>();
        data.put("booking_id",       bookingId);
        data.put("slot_id",          slotId);
        data.put("duration_minutes", durationMinutes);
        data.put("estimated_amount", estimatedAmount);
        data.put("status",           "reserved");

        return ResponseEntity.ok(ApiResponse.success(data, "Slot booked successfully! Proceed to payment."));
    }

    /**
     * POST /api/releaseSlot
     * Body: { "booking_id": 1 }
     *
     * 1. Computes actual duration and billing
     * 2. Marks booking as 'completed'
     * 3. Frees the slot
     * 4. Returns final_amount
     */
    @PostMapping("/releaseSlot")
    public ResponseEntity<ApiResponse<Map<String, Object>>> releaseSlot(
            @RequestBody Map<String, Object> body) {

        int bookingId = Integer.parseInt(body.get("booking_id").toString());
        BigDecimal finalAmount = bookingService.releaseSlot(bookingId);

        Map<String, Object> data = new HashMap<>();
        data.put("booking_id",   bookingId);
        data.put("final_amount", finalAmount);
        data.put("status",       "completed");
        data.put("currency",     "INR");

        return ResponseEntity.ok(ApiResponse.success(data, "Slot released. Total charge: ₹" + finalAmount));
    }

    /**
     * GET /api/bookings/{user_id}
     * Returns all bookings for a user (latest first).
     */
    @GetMapping("/bookings/{userId}")
    public ResponseEntity<ApiResponse<List<Booking>>> getBookings(@PathVariable int userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(bookings, "Found " + bookings.size() + " booking(s)"));
    }

    /**
     * GET /api/bookings/detail/{booking_id}
     * Returns a single booking (used by timer.html for session info).
     */
    @GetMapping("/bookings/detail/{bookingId}")
    public ResponseEntity<ApiResponse<Booking>> getBookingDetail(@PathVariable int bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(ApiResponse.success(booking));
    }

    /**
     * POST /api/bookings/{booking_id}/cancel
     */
    @PostMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelBooking(@PathVariable int bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.success(null, "Booking cancelled."));
    }
}
