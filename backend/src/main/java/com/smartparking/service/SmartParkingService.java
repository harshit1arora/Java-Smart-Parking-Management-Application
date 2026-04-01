package com.smartparking.service;

import com.smartparking.model.ParkingArea;
import com.smartparking.repository.ParkingAreaRepository;
import com.smartparking.repository.ParkingSlotRepository;
import com.smartparking.model.ParkingSlot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmartParkingService {

    private final ParkingAreaRepository parkingAreaRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public SmartParkingService(ParkingAreaRepository parkingAreaRepository,
                               ParkingSlotRepository parkingSlotRepository) {
        this.parkingAreaRepository = parkingAreaRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    public List<ParkingArea> getHotspots() {
        return parkingAreaRepository.findHotspots();
    }

    public List<ParkingArea> searchNearby(double lat, double lng, double radiusKm) {
        // Basic distance-based search (simulation)
        return parkingAreaRepository.findAll().stream()
                .filter(area -> calculateDistance(lat, lng, area.getLat(), area.getLng()) <= radiusKm)
                .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Simple Euclidean distance for simulation (can use Haversine for real apps)
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2)) * 111.0;
    }

    public void handleEmergencyVehicle(int areaId) {
        // Logic to prioritize slots for emergency vehicles
        // For example, if no emergency slots are free, free up a regular slot.
        List<ParkingSlot> slots = parkingSlotRepository.findByAreaId(areaId);
        boolean emergencySlotFree = slots.stream()
                .anyMatch(s -> s.getSlotType().equalsIgnoreCase("emergency") && s.getStatus().equalsIgnoreCase("FREE"));

        if (!emergencySlotFree) {
            // Find a regular free slot and mark it for emergency
            slots.stream()
                    .filter(s -> s.getSlotType().equalsIgnoreCase("regular") && s.getStatus().equalsIgnoreCase("FREE"))
                    .findFirst()
                    .ifPresent(s -> parkingSlotRepository.updateStatus(s.getSlotId(), "RESERVED"));
        }
    }
}
