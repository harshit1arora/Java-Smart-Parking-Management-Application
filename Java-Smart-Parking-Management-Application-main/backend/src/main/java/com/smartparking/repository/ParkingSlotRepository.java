package com.smartparking.repository;

import com.smartparking.model.ParkingSlot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParkingSlotRepository {

    private final JdbcTemplate jdbc;

    public ParkingSlotRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ParkingSlot> findByAreaId(int areaId) {
        String sql = "SELECT * FROM parking_slots WHERE area_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            ParkingSlot slot = new ParkingSlot();
            slot.setSlotId(rs.getInt("slot_id"));
            slot.setAreaId(rs.getInt("area_id"));
            slot.setSlotNumber(rs.getString("slot_number"));
            slot.setSlotType(rs.getString("slot_type"));
            slot.setStatus(rs.getString("status"));
            slot.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return slot;
        }, areaId);
    }

    public Optional<ParkingSlot> findById(int slotId) {
        String sql = "SELECT * FROM parking_slots WHERE slot_id = ?";
        List<ParkingSlot> results = jdbc.query(sql, (rs, rowNum) -> {
            ParkingSlot slot = new ParkingSlot();
            slot.setSlotId(rs.getInt("slot_id"));
            slot.setAreaId(rs.getInt("area_id"));
            slot.setSlotNumber(rs.getString("slot_number"));
            slot.setSlotType(rs.getString("slot_type"));
            slot.setStatus(rs.getString("status"));
            slot.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return slot;
        }, slotId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public void updateStatus(int slotId, String status) {
        String sql = "UPDATE parking_slots SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE slot_id = ?";
        jdbc.update(sql, status.toUpperCase(), slotId);
    }
}
