package Models;

import java.sql.Timestamp;

public class OTPToken {
    private int tokenId;
    private int userId;
    private String otpCode;
    private String tokenType;
    private Timestamp expirationTime;
    private boolean isUsed;
    private Timestamp createdAt;

    public OTPToken() {}

    public OTPToken(int userId, String otpCode, String tokenType, Timestamp expirationTime) {
        this.userId = userId;
        this.otpCode = otpCode;
        this.tokenType = tokenType;
        this.expirationTime = expirationTime;
        this.isUsed = false;
    }

    // Getters và Setters
    public int getTokenId() { return tokenId; }
    public void setTokenId(int tokenId) { this.tokenId = tokenId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Timestamp getExpirationTime() { return expirationTime; }
    public void setExpirationTime(Timestamp expirationTime) { this.expirationTime = expirationTime; }
    public boolean isUsed() { return isUsed; }
    public void setUsed(boolean isUsed) { this.isUsed = isUsed; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}