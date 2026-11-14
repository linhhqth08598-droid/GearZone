package fourSome.model;

public class PartReviewImages {
    private Integer imageId;
    private Integer reviewId;
    private String imageBase64; // Chứa chuỗi Base64
    private String status;

    public PartReviewImages() {}

    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }

    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}