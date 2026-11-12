package Models;


public class SpecRam {

    private Integer partId;
    private String ddrType;
    private Integer capacityGb;
    private Integer busSpeedMhz;
    private Integer moduleCount;
    private String casLatency;
    private String status;

    public SpecRam() {
    }

    public SpecRam(Integer partId, String ddrType, Integer capacityGb, Integer busSpeedMhz, Integer moduleCount, String casLatency, String status) {
        this.partId = partId;
        this.ddrType = ddrType;
        this.capacityGb = capacityGb;
        this.busSpeedMhz = busSpeedMhz;
        this.moduleCount = moduleCount;
        this.casLatency = casLatency;
        this.status = status;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getDdrType() {
        return ddrType;
    }

    public void setDdrType(String ddrType) {
        this.ddrType = ddrType;
    }

    public Integer getCapacityGb() {
        return capacityGb;
    }

    public void setCapacityGb(Integer capacityGb) {
        this.capacityGb = capacityGb;
    }

    public Integer getBusSpeedMhz() {
        return busSpeedMhz;
    }

    public void setBusSpeedMhz(Integer busSpeedMhz) {
        this.busSpeedMhz = busSpeedMhz;
    }

    public Integer getModuleCount() {
        return moduleCount;
    }

    public void setModuleCount(Integer moduleCount) {
        this.moduleCount = moduleCount;
    }

    public String getCasLatency() {
        return casLatency;
    }

    public void setCasLatency(String casLatency) {
        this.casLatency = casLatency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
