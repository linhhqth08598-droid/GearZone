package fourSome.model;


public class SpecCpu {

    private Integer partId;
    private Integer socketId;
    private Integer cores;
    private Integer threads;
    private Double baseClockGhz;
    private Double boostClockGhz;
    private Integer tdpWatts;
    private Boolean hasIntegratedGpu;
    private String supportedRamType;
    private String generation;
    private String status;

    public SpecCpu() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getSocketId() { return socketId; }
    public void setSocketId(Integer socketId) { this.socketId = socketId; }

    public Integer getCores() { return cores; }
    public void setCores(Integer cores) { this.cores = cores; }

    public Integer getThreads() { return threads; }
    public void setThreads(Integer threads) { this.threads = threads; }

    public Double getBaseClockGhz() { return baseClockGhz; }
    public void setBaseClockGhz(Double baseClockGhz) { this.baseClockGhz = baseClockGhz; }

    public Double getBoostClockGhz() { return boostClockGhz; }
    public void setBoostClockGhz(Double boostClockGhz) { this.boostClockGhz = boostClockGhz; }

    public Integer getTdpWatts() { return tdpWatts; }
    public void setTdpWatts(Integer tdpWatts) { this.tdpWatts = tdpWatts; }

    public Boolean getHasIntegratedGpu() { return hasIntegratedGpu; }
    public void setHasIntegratedGpu(Boolean hasIntegratedGpu) { this.hasIntegratedGpu = hasIntegratedGpu; }

    public String getSupportedRamType() { return supportedRamType; }
    public void setSupportedRamType(String supportedRamType) { this.supportedRamType = supportedRamType; }

    public String getGeneration() { return generation; }
    public void setGeneration(String generation) { this.generation = generation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
