package fourSome.model;


public class SpecStorage {

    private Integer partId;
    private String storageType;
    private String formFactor;
    private String _interface;
    private Integer capacityGb;
    private Integer readSpeedMbps;
    private Integer writeSpeedMbps;
    private String status;

    public SpecStorage() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public String getStorageType() { return storageType; }
    public void setStorageType(String storageType) { this.storageType = storageType; }

    public String getFormFactor() { return formFactor; }
    public void setFormFactor(String formFactor) { this.formFactor = formFactor; }

    public String get_interface() { return _interface; }
    public void set_interface(String _interface) { this._interface = _interface; }

    public Integer getCapacityGb() { return capacityGb; }
    public void setCapacityGb(Integer capacityGb) { this.capacityGb = capacityGb; }

    public Integer getReadSpeedMbps() { return readSpeedMbps; }
    public void setReadSpeedMbps(Integer readSpeedMbps) { this.readSpeedMbps = readSpeedMbps; }

    public Integer getWriteSpeedMbps() { return writeSpeedMbps; }
    public void setWriteSpeedMbps(Integer writeSpeedMbps) { this.writeSpeedMbps = writeSpeedMbps; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
