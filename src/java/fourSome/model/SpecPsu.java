package fourSome.model;


public class SpecPsu {

    private Integer partId;
    private Integer wattage;
    private String efficiencyRating;
    private String modularity;
    private Integer pcieConnectorsCount;
    private Boolean has12vhpwr;
    private Integer epsConnectorsCount;
    private String formFactor;
    private String status;

    public SpecPsu() {}

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }

    public Integer getWattage() { return wattage; }
    public void setWattage(Integer wattage) { this.wattage = wattage; }

    public String getEfficiencyRating() { return efficiencyRating; }
    public void setEfficiencyRating(String efficiencyRating) { this.efficiencyRating = efficiencyRating; }

    public String getModularity() { return modularity; }
    public void setModularity(String modularity) { this.modularity = modularity; }

    public Integer getPcieConnectorsCount() { return pcieConnectorsCount; }
    public void setPcieConnectorsCount(Integer pcieConnectorsCount) { this.pcieConnectorsCount = pcieConnectorsCount; }

    public Boolean getHas12vhpwr() { return has12vhpwr; }
    public void setHas12vhpwr(Boolean has12vhpwr) { this.has12vhpwr = has12vhpwr; }

    public Integer getEpsConnectorsCount() { return epsConnectorsCount; }
    public void setEpsConnectorsCount(Integer epsConnectorsCount) { this.epsConnectorsCount = epsConnectorsCount; }

    public String getFormFactor() { return formFactor; }
    public void setFormFactor(String formFactor) { this.formFactor = formFactor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
