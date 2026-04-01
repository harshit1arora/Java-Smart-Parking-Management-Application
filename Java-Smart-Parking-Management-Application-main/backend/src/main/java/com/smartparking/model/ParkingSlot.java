package com.smartparking.model;

import java.time.LocalDateTime;

public class ParkingSlot {
    private Integer       slotId;
    private Integer       areaId;
    private String        slotNumber;
    private String        slotType; // regular, emergency
    private String        status;   // FREE, OCCUPIED, RESERVED
    private LocalDateTime updatedAt;

    public ParkingSlot() {}

    public Integer getSlotId()                         { return slotId; }
    public void setSlotId(Integer slotId)               { this.slotId = slotId; }
    public Integer getAreaId()                         { return areaId; }
    public void setAreaId(Integer areaId)               { this.areaId = areaId; }
    public String getSlotNumber()                      { return slotNumber; }
    public void setSlotNumber(String slotNumber)       { this.slotNumber = slotNumber; }
    public String getSlotType()                        { return slotType; }
    public void setSlotType(String slotType)           { this.slotType = slotType; }
    public String getStatus()                          { return status; }
    public void setStatus(String status)               { this.status = status; }
    public LocalDateTime getUpdatedAt()                { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt)  { this.updatedAt = updatedAt; }
}
