package com.smartparking.repository;

import com.smartparking.model.Payment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

/**
 * PaymentRepository — JDBC-based data access for payments.
 */
@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbc;

    public PaymentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** Insert a new payment record and return the generated payment_id */
    @Transactional
    public int save(int bookingId, BigDecimal amount, String method, String orderId) {
        String sql = "INSERT INTO payments (booking_id, amount, method, status, order_id) VALUES (?,?,?,'pending',?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"payment_id"});
            ps.setInt(1, bookingId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, method);
            ps.setString(4, orderId);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Optional<Payment> findByOrderId(String orderId) {
        String sql = "SELECT * FROM payments WHERE order_id = ?";
        List<Payment> results = jdbc.query(sql, (rs, rowNum) -> {
            Payment p = new Payment();
            p.setPaymentId(rs.getInt("payment_id"));
            p.setBookingId(rs.getInt("booking_id"));
            p.setAmount(rs.getBigDecimal("amount"));
            p.setMethod(rs.getString("method"));
            p.setStatus(rs.getString("status"));
            p.setTransactionId(rs.getString("transaction_id"));
            p.setOrderId(rs.getString("order_id"));
            return p;
        }, orderId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /** Mark payment as successful */
    @Transactional
    public void markSuccess(String orderId, String transactionId) {
        jdbc.update(
                "UPDATE payments SET status = 'success', transaction_id = ? WHERE order_id = ?",
                transactionId, orderId
        );
    }

    public void markFailed(String orderId) {
        jdbc.update("UPDATE payments SET status = 'failed' WHERE order_id = ?", orderId);
    }
}
