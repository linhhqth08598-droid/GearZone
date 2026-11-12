package Models;


public class SpecCase {

    private Integer partId;
    private String supportedMotherboards;
    private Integer maxVgaLengthMm;
    private Integer maxCpuCoolerHeightMm;
    private String fanSupportInfo;
    private String status;

    public SpecCase() {
    }

    public SpecCase(Integer partId, String supportedMotherboards, Integer maxVgaLengthMm, Integer maxCpuCoolerHeightMm, String fanSupportInfo, String status) {
        this.partId = partId;
        this.supportedMotherboards = supportedMotherboards;
        this.maxVgaLengthMm = maxVgaLengthMm;
        this.maxCpuCoolerHeightMm = maxCpuCoolerHeightMm;
        this.fanSupportInfo = fanSupportInfo;
        this.status = status;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getSupportedMotherboards() {
        return supportedMotherboards;
    }

    public void setSupportedMotherboards(String supportedMotherboards) {
        this.supportedMotherboards = supportedMotherboards;
    }

    public Integer getMaxVgaLengthMm() {
        return maxVgaLengthMm;
    }

    public void setMaxVgaLengthMm(Integer maxVgaLengthMm) {
        this.maxVgaLengthMm = maxVgaLengthMm;
    }

    public Integer getMaxCpuCoolerHeightMm() {
        return maxCpuCoolerHeightMm;
    }

    public void setMaxCpuCoolerHeightMm(Integer maxCpuCoolerHeightMm) {
        this.maxCpuCoolerHeightMm = maxCpuCoolerHeightMm;
    }

    public String getFanSupportInfo() {
        return fanSupportInfo;
    }

    public void setFanSupportInfo(String fanSupportInfo) {
        this.fanSupportInfo = fanSupportInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
