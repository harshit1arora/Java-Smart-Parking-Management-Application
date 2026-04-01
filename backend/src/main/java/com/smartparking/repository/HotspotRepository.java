package com.smartparking.repository;

import com.smartparking.model.Hotspot;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HotspotRepository {

    private final JdbcTemplate jdbc;

    public HotspotRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Hotspot> findByAreaId(int areaId) {
        String sql = "SELECT * FROM hotspots WHERE area_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            Hotspot h = new Hotspot();
            h.setHotspotId(rs.getInt("hotspot_id"));
            h.setAreaId(rs.getInt("area_id"));
            h.setPeakStart(rs.getTime("peak_start").toLocalTime());
            h.setPeakEnd(rs.getTime("peak_end").toLocalTime());
            h.setDemandScore(rs.getInt("demand_score"));
            return h;
        }, areaId);
    }

    public void updateDemandScore(int areaId, int score) {
        String sql = "UPDATE hotspots SET demand_score = ? WHERE area_id = ?";
        jdbc.update(sql, score, areaId);
    }
}
