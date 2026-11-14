package fourSome.model;

import java.util.Date;
import java.util.List;

public class PcBuildHistoryItem {
    private int buildId;
    private String buildName;
    private double totalPrice;
    private Date createdAt;
    private String status;
    private String shareToken;
    
    // Danh sách linh kiện chi tiết
    private List<PcBuildDetailItem> details;

    // Getters and Setters
    public int getBuildId() { return buildId; }
    public void setBuildId(int buildId) { this.buildId = buildId; }

    public String getBuildName() { return buildName; }
    public void setBuildName(String buildName) { this.buildName = buildName; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getShareToken() { return shareToken; }
    public void setShareToken(String shareToken) { this.shareToken = shareToken; }

    public List<PcBuildDetailItem> getDetails() { return details; }
    public void setDetails(List<PcBuildDetailItem> details) { this.details = details; }
}