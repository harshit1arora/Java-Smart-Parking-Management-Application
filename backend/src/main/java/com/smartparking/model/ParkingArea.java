package com.smartparking.model;

import java.time.LocalDateTime;

public class ParkingArea {
    private Integer       areaId;
    private String        name;
    private String        location;
    private Integer       totalSlots;
    private Double        lat;
    private Double        lng;
    private Boolean       isHotspot;
    private LocalDateTime createdAt;

    public ParkingArea() {}

    public Integer getAreaId()                         { return areaId; }
    public void setAreaId(Integer areaId)               { this.areaId = areaId; }
    public String getName()                            { return name; }
    public void setName(String name)                   { this.name = name; }
    public String getLocation()                        { return location; }
    public void setLocation(String location)           { this.location = location; }
    public Integer getTotalSlots()                     { return totalSlots; }
    public void setTotalSlots(Integer totalSlots)      { this.totalSlots = totalSlots; }
    public Double getLat()                             { return lat; }
    public void setLat(Double lat)                     { this.lat = lat; }
    public Double getLng()                             { return lng; }
    public void setLng(Double lng)                     { this.lng = lng; }
    public Boolean getIsHotspot()                      { return isHotspot; }
    public void setIsHotspot(Boolean hotspot)          { isHotspot = hotspot; }
    public LocalDateTime getCreatedAt()                { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)  { this.createdAt = createdAt; }
}
