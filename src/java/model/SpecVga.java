package model;


public class SpecVga {

    private Integer partId;
    private String gpuChipset;
    private Integer vramCapacityGb;
    private String vramType;
    private Integer recommendedPsuWatts;
    private Integer tgpWatts;
    private String powerConnectors;
    private Integer lengthMm;
    private String pcieInterface;
    private Integer hdmiPorts;
    private Integer displayportPorts;
    private String status;

    public SpecVga() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getGpuChipset() { return gpuChipset; }
    public void setGpuChipset(String gpuChipset) { this.gpuChipset = gpuChipset; }

    public Integer getVramCapacityGb() { return vramCapacityGb; }
    public void setVramCapacityGb(Integer vramCapacityGb) { this.vramCapacityGb = vramCapacityGb; }

    public String getVramType() { return vramType; }
    public void setVramType(String vramType) { this.vramType = vramType; }

    public Integer getRecommendedPsuWatts() { return recommendedPsuWatts; }
    public void setRecommendedPsuWatts(Integer recommendedPsuWatts) { this.recommendedPsuWatts = recommendedPsuWatts; }

    public Integer getTgpWatts() { return tgpWatts; }
    public void setTgpWatts(Integer tgpWatts) { this.tgpWatts = tgpWatts; }

    public String getPowerConnectors() { return powerConnectors; }
    public void setPowerConnectors(String powerConnectors) { this.powerConnectors = powerConnectors; }

    public Integer getLengthMm() { return lengthMm; }
    public void setLengthMm(Integer lengthMm) { this.lengthMm = lengthMm; }

    public String getPcieInterface() { return pcieInterface; }
    public void setPcieInterface(String pcieInterface) { this.pcieInterface = pcieInterface; }

    public Integer getHdmiPorts() { return hdmiPorts; }
    public void setHdmiPorts(Integer hdmiPorts) { this.hdmiPorts = hdmiPorts; }

    public Integer getDisplayportPorts() { return displayportPorts; }
    public void setDisplayportPorts(Integer displayportPorts) { this.displayportPorts = displayportPorts; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
