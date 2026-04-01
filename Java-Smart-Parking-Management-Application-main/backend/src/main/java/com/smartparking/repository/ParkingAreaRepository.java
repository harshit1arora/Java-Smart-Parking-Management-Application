package com.smartparking.repository;

import com.smartparking.model.ParkingArea;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParkingAreaRepository {

    private final JdbcTemplate jdbc;

    public ParkingAreaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<ParkingArea> findAll() {
        String sql = "SELECT * FROM parking_areas";
        return jdbc.query(sql, (rs, rowNum) -> {
            ParkingArea area = new ParkingArea();
            area.setAreaId(rs.getInt("area_id"));
            area.setName(rs.getString("name"));
            area.setLocation(rs.getString("location"));
            area.setTotalSlots(rs.getInt("total_slots"));
            area.setLat(rs.getDouble("lat"));
            area.setLng(rs.getDouble("lng"));
            area.setIsHotspot(rs.getBoolean("is_hotspot"));
            area.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return area;
        });
    }

    public List<ParkingArea> findHotspots() {
        String sql = "SELECT * FROM parking_areas WHERE is_hotspot = TRUE";
        return jdbc.query(sql, (rs, rowNum) -> {
            ParkingArea area = new ParkingArea();
            area.setAreaId(rs.getInt("area_id"));
            area.setName(rs.getString("name"));
            area.setLocation(rs.getString("location"));
            area.setTotalSlots(rs.getInt("total_slots"));
            area.setLat(rs.getDouble("lat"));
            area.setLng(rs.getDouble("lng"));
            area.setIsHotspot(rs.getBoolean("is_hotspot"));
            return area;
        });
    }

    public Optional<ParkingArea> findById(int areaId) {
        String sql = "SELECT * FROM parking_areas WHERE area_id = ?";
        List<ParkingArea> results = jdbc.query(sql, (rs, rowNum) -> {
            ParkingArea area = new ParkingArea();
            area.setAreaId(rs.getInt("area_id"));
            area.setName(rs.getString("name"));
            area.setLocation(rs.getString("location"));
            area.setTotalSlots(rs.getInt("total_slots"));
            area.setLat(rs.getDouble("lat"));
            area.setLng(rs.getDouble("lng"));
            area.setIsHotspot(rs.getBoolean("is_hotspot"));
            return area;
        }, areaId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
