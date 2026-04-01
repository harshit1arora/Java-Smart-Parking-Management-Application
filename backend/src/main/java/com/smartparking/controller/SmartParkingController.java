package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.model.ParkingArea;
import com.smartparking.service.SmartParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/smart")
public class SmartParkingController {

    private final SmartParkingService smartParkingService;

    public SmartParkingController(SmartParkingService smartParkingService) {
        this.smartParkingService = smartParkingService;
    }

    @GetMapping("/hotspots")
    public ResponseEntity<ApiResponse<List<ParkingArea>>> getHotspots() {
        List<ParkingArea> hotspots = smartParkingService.getHotspots();
        return ResponseEntity.ok(ApiResponse.success(hotspots, "Found " + hotspots.size() + " hotspots"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ParkingArea>>> searchNearby(
            @RequestParam double lat, @RequestParam double lng, @RequestParam(defaultValue = "10.0") double radius) {
        List<ParkingArea> areas = smartParkingService.searchNearby(lat, lng, radius);
        return ResponseEntity.ok(ApiResponse.success(areas, "Found " + areas.size() + " areas within " + radius + " km"));
    }

    @PostMapping("/emergency")
    public ResponseEntity<ApiResponse<String>> handleEmergency(@RequestBody Map<String, Integer> body) {
        int areaId = body.get("area_id");
        smartParkingService.handleEmergencyVehicle(areaId);
        return ResponseEntity.ok(ApiResponse.success(null, "Emergency vehicle priority handling initiated for area: " + areaId));
    }
}
