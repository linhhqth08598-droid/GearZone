package Models;


public class SpecMainboard {

    private Integer partId;
    private Integer socketId;
    private String chipset;
    private String formFactor;
    private String ramStandard;
    private Integer ramSlots;
    private Integer maxRamSpeedMhz;
    private Integer maxRamCapacityGb;
    private Integer m2SlotsCount;
    private Boolean supportsNvme;
    private String pcieGeneration;
    private Integer sataPortsCount;
    private String cpuPowerConnectors;
    private String supportedCpuGenerations;
    private String status;

    public SpecMainboard() {
    }

    public SpecMainboard(Integer partId, Integer socketId, String chipset, String formFactor, String ramStandard, Integer ramSlots, Integer maxRamSpeedMhz, Integer maxRamCapacityGb, Integer m2SlotsCount, Boolean supportsNvme, String pcieGeneration, Integer sataPortsCount, String cpuPowerConnectors, String supportedCpuGenerations, String status) {
        this.partId = partId;
        this.socketId = socketId;
        this.chipset = chipset;
        this.formFactor = formFactor;
        this.ramStandard = ramStandard;
        this.ramSlots = ramSlots;
        this.maxRamSpeedMhz = maxRamSpeedMhz;
        this.maxRamCapacityGb = maxRamCapacityGb;
        this.m2SlotsCount = m2SlotsCount;
        this.supportsNvme = supportsNvme;
        this.pcieGeneration = pcieGeneration;
        this.sataPortsCount = sataPortsCount;
        this.cpuPowerConnectors = cpuPowerConnectors;
        this.supportedCpuGenerations = supportedCpuGenerations;
        this.status = status;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getSocketId() {
        return socketId;
    }

    public void setSocketId(Integer socketId) {
        this.socketId = socketId;
    }

    public String getChipset() {
        return chipset;
    }

    public void setChipset(String chipset) {
        this.chipset = chipset;
    }

    public String getFormFactor() {
        return formFactor;
    }

    public void setFormFactor(String formFactor) {
        this.formFactor = formFactor;
    }

    public String getRamStandard() {
        return ramStandard;
    }

    public void setRamStandard(String ramStandard) {
        this.ramStandard = ramStandard;
    }

    public Integer getRamSlots() {
        return ramSlots;
    }

    public void setRamSlots(Integer ramSlots) {
        this.ramSlots = ramSlots;
    }

    public Integer getMaxRamSpeedMhz() {
        return maxRamSpeedMhz;
    }

    public void setMaxRamSpeedMhz(Integer maxRamSpeedMhz) {
        this.maxRamSpeedMhz = maxRamSpeedMhz;
    }

    public Integer getMaxRamCapacityGb() {
        return maxRamCapacityGb;
    }

    public void setMaxRamCapacityGb(Integer maxRamCapacityGb) {
        this.maxRamCapacityGb = maxRamCapacityGb;
    }

    public Integer getM2SlotsCount() {
        return m2SlotsCount;
    }

    public void setM2SlotsCount(Integer m2SlotsCount) {
        this.m2SlotsCount = m2SlotsCount;
    }

    public Boolean getSupportsNvme() {
        return supportsNvme;
    }

    public void setSupportsNvme(Boolean supportsNvme) {
        this.supportsNvme = supportsNvme;
    }

    public String getPcieGeneration() {
        return pcieGeneration;
    }

    public void setPcieGeneration(String pcieGeneration) {
        this.pcieGeneration = pcieGeneration;
    }

    public Integer getSataPortsCount() {
        return sataPortsCount;
    }

    public void setSataPortsCount(Integer sataPortsCount) {
        this.sataPortsCount = sataPortsCount;
    }

    public String getCpuPowerConnectors() {
        return cpuPowerConnectors;
    }

    public void setCpuPowerConnectors(String cpuPowerConnectors) {
        this.cpuPowerConnectors = cpuPowerConnectors;
    }

    public String getSupportedCpuGenerations() {
        return supportedCpuGenerations;
    }

    public void setSupportedCpuGenerations(String supportedCpuGenerations) {
        this.supportedCpuGenerations = supportedCpuGenerations;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
