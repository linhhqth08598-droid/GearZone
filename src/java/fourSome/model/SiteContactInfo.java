package fourSome.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SiteContactInfo {
    private Integer contactInfoId;
    private String companyName;
    private String introText;
    private String address1;
    private String address2;
    private List<String> addresses = new ArrayList<>();
    private String salesHours;
    private String techHours;
    private String hotline;
    private String facebookUrl;
    private String youtubeUrl;
    private String chatUrl;
    private Date updatedAt;
    private Integer updatedBy;

    public Integer getContactInfoId() { return contactInfoId; }
    public void setContactInfoId(Integer contactInfoId) { this.contactInfoId = contactInfoId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIntroText() { return introText; }
    public void setIntroText(String introText) { this.introText = introText; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public List<String> getAddresses() { return addresses; }
    public void setAddresses(List<String> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
    }

    public String getSalesHours() { return salesHours; }
    public void setSalesHours(String salesHours) { this.salesHours = salesHours; }

    public String getTechHours() { return techHours; }
    public void setTechHours(String techHours) { this.techHours = techHours; }

    public String getHotline() { return hotline; }
    public void setHotline(String hotline) { this.hotline = hotline; }

    public String getFacebookUrl() { return facebookUrl; }
    public void setFacebookUrl(String facebookUrl) { this.facebookUrl = facebookUrl; }

    public String getYoutubeUrl() { return youtubeUrl; }
    public void setYoutubeUrl(String youtubeUrl) { this.youtubeUrl = youtubeUrl; }

    public String getChatUrl() { return chatUrl; }
    public void setChatUrl(String chatUrl) { this.chatUrl = chatUrl; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
}