package fourSome.model;

import java.util.Date;

public class PartReviews {

    private Integer reviewId;
    private Integer userId;
    private String userName;
    private Integer partId;
    private String partName;
    private Integer orderId;
    private Integer qualityRating;
    private Integer performanceRating;
    private String comment;
    private String status;
    private Date createdAt;
    private String imageUrl; 

    public PartReviews() {}

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getPartId() { return partId; }
    public void setPartId(Integer partId) { this.partId = partId; }
    
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }

    public Integer getQualityRating() { return qualityRating; }
    public void setQualityRating(Integer qualityRating) { this.qualityRating = qualityRating; }

    public Integer getPerformanceRating() { return performanceRating; }
    public void setPerformanceRating(Integer performanceRating) { this.performanceRating = performanceRating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
