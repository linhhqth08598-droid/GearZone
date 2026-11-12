package Models;


public class SpecPeripheral {

    private Integer partId;
    private String peripheralType;
    private String connectionType;
    private String switchType;
    private String layout;
    private Integer dpiMax;
    private Integer pollingRateHz;
    private Integer weightG;
    private String status;

    public SpecPeripheral() {
    }

    public SpecPeripheral(Integer partId, String peripheralType, String connectionType, String switchType, String layout, Integer dpiMax, Integer pollingRateHz, Integer weightG, String status) {
        this.partId = partId;
        this.peripheralType = peripheralType;
        this.connectionType = connectionType;
        this.switchType = switchType;
        this.layout = layout;
        this.dpiMax = dpiMax;
        this.pollingRateHz = pollingRateHz;
        this.weightG = weightG;
        this.status = status;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public String getPeripheralType() {
        return peripheralType;
    }

    public void setPeripheralType(String peripheralType) {
        this.peripheralType = peripheralType;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public Integer getDpiMax() {
        return dpiMax;
    }

    public void setDpiMax(Integer dpiMax) {
        this.dpiMax = dpiMax;
    }

    public Integer getPollingRateHz() {
        return pollingRateHz;
    }

    public void setPollingRateHz(Integer pollingRateHz) {
        this.pollingRateHz = pollingRateHz;
    }

    public Integer getWeightG() {
        return weightG;
    }

    public void setWeightG(Integer weightG) {
        this.weightG = weightG;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
