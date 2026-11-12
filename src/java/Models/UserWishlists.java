package Models;

import java.util.Date;

public class UserWishlists {

    private Integer wishlistId;
    private Integer userId;
    private Integer partId;
    private Date createdAt;
    private String status;

    public UserWishlists() {
    }

    public UserWishlists(Integer wishlistId, Integer userId, Integer partId, Date createdAt, String status) {
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.partId = partId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(Integer wishlistId) {
        this.wishlistId = wishlistId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
