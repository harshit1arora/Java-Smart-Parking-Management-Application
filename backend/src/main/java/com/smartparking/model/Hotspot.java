package com.smartparking.model;

import java.time.LocalTime;

public class Hotspot {
    private Integer   hotspotId;
    private Integer   areaId;
    private LocalTime peakStart;
    private LocalTime peakEnd;
    private Integer   demandScore;

    public Hotspot() {}

    public Integer getHotspotId()              { return hotspotId; }
    public void setHotspotId(Integer hotspotId) { this.hotspotId = hotspotId; }
    public Integer getAreaId()                 { return areaId; }
    public void setAreaId(Integer areaId)       { this.areaId = areaId; }
    public LocalTime getPeakStart()            { return peakStart; }
    public void setPeakStart(LocalTime peakStart) { this.peakStart = peakStart; }
    public LocalTime getPeakEnd()              { return peakEnd; }
    public void setPeakEnd(LocalTime peakEnd)     { this.peakEnd = peakEnd; }
    public Integer getDemandScore()            { return demandScore; }
    public void setDemandScore(Integer score)   { this.demandScore = score; }
}
