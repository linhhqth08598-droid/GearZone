package model;


public class SpecCooler {

    private Integer partId;
    private String coolerType;
    private Integer heightMm;
    private Integer radiatorSizeMm;
    private String supportedSockets;
    private Integer maxTdpCoolingWatts;
    private String status;

    public SpecCooler() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getCoolerType() { return coolerType; }
    public void setCoolerType(String coolerType) { this.coolerType = coolerType; }

    public Integer getHeightMm() { return heightMm; }
    public void setHeightMm(Integer heightMm) { this.heightMm = heightMm; }

    public Integer getRadiatorSizeMm() { return radiatorSizeMm; }
    public void setRadiatorSizeMm(Integer radiatorSizeMm) { this.radiatorSizeMm = radiatorSizeMm; }

    public String getSupportedSockets() { return supportedSockets; }
    public void setSupportedSockets(String supportedSockets) { this.supportedSockets = supportedSockets; }

    public Integer getMaxTdpCoolingWatts() { return maxTdpCoolingWatts; }
    public void setMaxTdpCoolingWatts(Integer maxTdpCoolingWatts) { this.maxTdpCoolingWatts = maxTdpCoolingWatts; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
