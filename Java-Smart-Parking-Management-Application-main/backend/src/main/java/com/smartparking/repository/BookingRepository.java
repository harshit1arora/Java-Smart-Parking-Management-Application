package com.smartparking.repository;

import com.smartparking.model.Booking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepository {

    private final JdbcTemplate jdbc;

    public BookingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int save(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, slot_id, start_time, duration_minutes, status) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"booking_id"});
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getSlotId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            ps.setInt(4, booking.getDurationMinutes());
            ps.setString(5, booking.getStatus());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public Optional<Booking> findById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        List<Booking> results = jdbc.query(sql, (rs, rowNum) -> {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("booking_id"));
            b.setUserId(rs.getInt("user_id"));
            b.setSlotId(rs.getInt("slot_id"));
            b.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            b.setDurationMinutes(rs.getInt("duration_minutes"));
            b.setStatus(rs.getString("status"));
            b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return b;
        }, bookingId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Booking> findByUserId(int userId) {
        String sql = "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC";
        return jdbc.query(sql, (rs, rowNum) -> {
            Booking b = new Booking();
            b.setBookingId(rs.getInt("booking_id"));
            b.setUserId(rs.getInt("user_id"));
            b.setSlotId(rs.getInt("slot_id"));
            b.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
            b.setDurationMinutes(rs.getInt("duration_minutes"));
            b.setStatus(rs.getString("status"));
            b.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return b;
        }, userId);
    }

    public void completeBooking(int bookingId, BigDecimal finalAmount) {
        String sql = "UPDATE bookings SET end_time = CURRENT_TIMESTAMP, final_amount = ?, status = 'COMPLETED' WHERE booking_id = ?";
        jdbc.update(sql, finalAmount, bookingId);
    }

    public void updateStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        jdbc.update(sql, status.toUpperCase(), bookingId);
    }
}
