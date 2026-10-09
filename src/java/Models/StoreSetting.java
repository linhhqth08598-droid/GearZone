package Models;

public class StoreSetting {
    private int settingId;
    private String companyName;
    private String businessLicense;
    private String address1;
    private String address2;
    private String hotline;
    private String websiteUrl;
    private String fanpageUrl;
    private String email;
    private String googleMapIframe;

    public StoreSetting() {}

    // Getters và Setters
    public int getSettingId() { return settingId; }
    public void setSettingId(int settingId) { this.settingId = settingId; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getBusinessLicense() { return businessLicense; }
    public void setBusinessLicense(String businessLicense) { this.businessLicense = businessLicense; }
    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }
    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }
    public String getHotline() { return hotline; }
    public void setHotline(String hotline) { this.hotline = hotline; }
    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }
    public String getFanpageUrl() { return fanpageUrl; }
    public void setFanpageUrl(String fanpageUrl) { this.fanpageUrl = fanpageUrl; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getGoogleMapIframe() { return googleMapIframe; }
    public void setGoogleMapIframe(String googleMapIframe) { this.googleMapIframe = googleMapIframe; }
}