package Models;

import java.util.Date;

public class Users {

    private Integer userId;
    private String email;
    private String phoneNumber;
    private String passwordHash;
    private String fullName;
    private String avatarUrl;
    private Boolean isEmailVerified;
    private Boolean isPhoneVerified;
    private Date lastLoginAt;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public Users() {
    }

    public Users(Integer userId, String email, String phoneNumber, String passwordHash, String fullName, String avatarUrl, Boolean isEmailVerified, Boolean isPhoneVerified, Date lastLoginAt, String status, Date createdAt, Date updatedAt) {
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.isEmailVerified = isEmailVerified;
        this.isPhoneVerified = isPhoneVerified;
        this.lastLoginAt = lastLoginAt;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Boolean getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}
