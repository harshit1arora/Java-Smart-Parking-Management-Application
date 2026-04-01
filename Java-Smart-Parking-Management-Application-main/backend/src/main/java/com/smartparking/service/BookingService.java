package com.smartparking.service;

import com.smartparking.model.Booking;
import com.smartparking.repository.BookingRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.util.BillingUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository     bookingRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final BillingUtil          billingUtil;

    public BookingService(BookingRepository bookingRepository,
                          ParkingSlotRepository parkingSlotRepository,
                          BillingUtil billingUtil) {
        this.bookingRepository     = bookingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.billingUtil          = billingUtil;
    }

    @Transactional
    public int bookSlot(int userId, int slotId, int durationMinutes) {
        parkingSlotRepository.updateStatus(slotId, "RESERVED");

        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setSlotId(slotId);
        booking.setStartTime(LocalDateTime.now());
        booking.setDurationMinutes(durationMinutes);
        booking.setStatus("RESERVED");

        return bookingRepository.save(booking);
    }

    @Transactional
    public BigDecimal releaseSlot(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            parkingSlotRepository.updateStatus(booking.getSlotId(), "FREE");
            
            BigDecimal finalAmount = billingUtil.computeFinalAmount(booking.getDurationMinutes());
            bookingRepository.completeBooking(bookingId, finalAmount);
            return finalAmount;
        }
        return BigDecimal.ZERO;
    }

    public List<Booking> getBookingsByUser(int userId) {
        return bookingRepository.findByUserId(userId);
    }

    public Booking getBookingById(int bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingId));
    }

    @Transactional
    public void cancelBooking(int bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            parkingSlotRepository.updateStatus(booking.getSlotId(), "FREE");
            bookingRepository.updateStatus(bookingId, "CANCELLED");
        }
    }
}
