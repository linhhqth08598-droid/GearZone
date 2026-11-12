package Models;


public class SpecMonitor {

    private Integer partId;
    private Double screenSizeInch;
    private String resolution;
    private Integer refreshRateHz;
    private String panelType;
    private Double responseTimeMs;
    private String adaptiveSync;
    private Integer hdmiPorts;
    private Integer displayportPorts;
    private String hdmiVersion;
    private String displayportVersion;
    private String status;

    public SpecMonitor() {
    }

    public SpecMonitor(Integer partId, Double screenSizeInch, String resolution, Integer refreshRateHz, String panelType, Double responseTimeMs, String adaptiveSync, Integer hdmiPorts, Integer displayportPorts, String hdmiVersion, String displayportVersion, String status) {
        this.partId = partId;
        this.screenSizeInch = screenSizeInch;
        this.resolution = resolution;
        this.refreshRateHz = refreshRateHz;
        this.panelType = panelType;
        this.responseTimeMs = responseTimeMs;
        this.adaptiveSync = adaptiveSync;
        this.hdmiPorts = hdmiPorts;
        this.displayportPorts = displayportPorts;
        this.hdmiVersion = hdmiVersion;
        this.displayportVersion = displayportVersion;
        this.status = status;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Double getScreenSizeInch() {
        return screenSizeInch;
    }

    public void setScreenSizeInch(Double screenSizeInch) {
        this.screenSizeInch = screenSizeInch;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Integer getRefreshRateHz() {
        return refreshRateHz;
    }

    public void setRefreshRateHz(Integer refreshRateHz) {
        this.refreshRateHz = refreshRateHz;
    }

    public String getPanelType() {
        return panelType;
    }

    public void setPanelType(String panelType) {
        this.panelType = panelType;
    }

    public Double getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Double responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getAdaptiveSync() {
        return adaptiveSync;
    }

    public void setAdaptiveSync(String adaptiveSync) {
        this.adaptiveSync = adaptiveSync;
    }

    public Integer getHdmiPorts() {
        return hdmiPorts;
    }

    public void setHdmiPorts(Integer hdmiPorts) {
        this.hdmiPorts = hdmiPorts;
    }

    public Integer getDisplayportPorts() {
        return displayportPorts;
    }

    public void setDisplayportPorts(Integer displayportPorts) {
        this.displayportPorts = displayportPorts;
    }

    public String getHdmiVersion() {
        return hdmiVersion;
    }

    public void setHdmiVersion(String hdmiVersion) {
        this.hdmiVersion = hdmiVersion;
    }

    public String getDisplayportVersion() {
        return displayportVersion;
    }

    public void setDisplayportVersion(String displayportVersion) {
        this.displayportVersion = displayportVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
