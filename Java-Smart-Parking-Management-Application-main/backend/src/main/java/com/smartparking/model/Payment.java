package com.smartparking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {
    private Integer    paymentId;
    private Integer    bookingId;
    private BigDecimal amount;
    private String     method;
    private String     status;
    private String     transactionId;
    private String     orderId;
    private LocalDateTime createdAt;

    public Payment() {}

    public Integer getPaymentId()                           { return paymentId; }
    public void setPaymentId(Integer paymentId)             { this.paymentId = paymentId; }
    public Integer getBookingId()                           { return bookingId; }
    public void setBookingId(Integer bookingId)             { this.bookingId = bookingId; }
    public BigDecimal getAmount()                           { return amount; }
    public void setAmount(BigDecimal amount)                { this.amount = amount; }
    public String getMethod()                               { return method; }
    public void setMethod(String method)                    { this.method = method; }
    public String getStatus()                               { return status; }
    public void setStatus(String status)                    { this.status = status; }
    public String getTransactionId()                                { return transactionId; }
    public void setTransactionId(String transactionId)              { this.transactionId = transactionId; }
    public String getOrderId()                              { return orderId; }
    public void setOrderId(String orderId)                  { this.orderId = orderId; }
    public LocalDateTime getCreatedAt()                     { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)       { this.createdAt = createdAt; }
}
