package fourSome.model;

import java.util.Date;

public class UserVerifications {

    private Integer verificationId;
    private Integer userId;
    private String type;
    private String code;
    private Date expiresAt;
    private Boolean isVerified;
    private String status;

    public UserVerifications() {}

    public Integer getVerificationId() { return verificationId; }
    public void setVerificationId(Integer verificationId) { this.verificationId = verificationId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
