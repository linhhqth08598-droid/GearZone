package Models;


public class PartReviewImages {

    private Integer imageId;
    private Integer reviewId;
    private String imageUrl;
    private String status;

    public PartReviewImages() {
    }

    public PartReviewImages(Integer imageId, Integer reviewId, String imageUrl, String status) {
        this.imageId = imageId;
        this.reviewId = reviewId;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
