package com.smartparking.controller;

import com.smartparking.exception.ApiResponse;
import com.smartparking.model.ParkingSlot;
import com.smartparking.repository.ParkingSlotRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final ParkingSlotRepository parkingSlotRepository;

    public SlotController(ParkingSlotRepository parkingSlotRepository) {
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ParkingSlot>>> getSlots(@RequestParam(required = false) Integer area_id) {
        List<ParkingSlot> slots;
        if (area_id != null) {
            slots = parkingSlotRepository.findByAreaId(area_id);
        } else {
            // Return all slots if no area_id is provided (optional)
            slots = List.of(); 
        }
        return ResponseEntity.ok(ApiResponse.success(slots, "Found " + slots.size() + " slots"));
    }
}
