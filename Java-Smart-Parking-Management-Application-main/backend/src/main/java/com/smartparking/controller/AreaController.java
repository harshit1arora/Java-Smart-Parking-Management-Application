package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.model.ParkingArea;
import com.smartparking.model.ParkingSlot;
import com.smartparking.repository.ParkingAreaRepository;
import com.smartparking.repository.ParkingSlotRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final ParkingAreaRepository parkingAreaRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public AreaController(ParkingAreaRepository parkingAreaRepository,
                          ParkingSlotRepository parkingSlotRepository) {
        this.parkingAreaRepository = parkingAreaRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingArea>>> getAllAreas(@RequestParam(required = false) String q) {
        List<ParkingArea> areas = parkingAreaRepository.findAll();
        if (q != null && !q.isEmpty()) {
            areas = areas.stream()
                    .filter(a -> a.getName().toLowerCase().contains(q.toLowerCase()) || 
                                 a.getLocation().toLowerCase().contains(q.toLowerCase()))
                    .collect(java.util.stream.Collectors.toList());
        }
        return ResponseEntity.ok(ApiResponse.success(areas, "Found " + areas.size() + " parking areas"));
    }

    @GetMapping("/{areaId}/slots")
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> getSlotsByArea(@PathVariable int areaId) {
        List<ParkingSlot> slots = parkingSlotRepository.findByAreaId(areaId);
        return ResponseEntity.ok(ApiResponse.success(slots, "Found " + slots.size() + " slots for area: " + areaId));
    }
}
