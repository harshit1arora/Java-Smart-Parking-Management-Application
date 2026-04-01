package com.smartparking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Booking {
    private Integer       bookingId;
    private Integer       userId;
    private Integer       slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer       durationMinutes;
    private BigDecimal    finalAmount;
    private String        status; // ACTIVE, COMPLETED, CANCELLED, RESERVED
    private LocalDateTime createdAt;

    public Booking() {}

    public Integer getBookingId()                           { return bookingId; }
    public void setBookingId(Integer bookingId)             { this.bookingId = bookingId; }
    public Integer getUserId()                              { return userId; }
    public void setUserId(Integer userId)                   { this.userId = userId; }
    public Integer getSlotId()                              { return slotId; }
    public void setSlotId(Integer slotId)                   { this.slotId = slotId; }
    public LocalDateTime getStartTime()                     { return startTime; }
    public void setStartTime(LocalDateTime startTime)       { this.startTime = startTime; }
    public LocalDateTime getEndTime()                       { return endTime; }
    public void setEndTime(LocalDateTime endTime)           { this.endTime = endTime; }
    public Integer getDurationMinutes()                     { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public BigDecimal getFinalAmount()                      { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount)      { this.finalAmount = finalAmount; }
    public String getStatus()                               { return status; }
    public void setStatus(String status)                    { this.status = status; }
    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }
}
