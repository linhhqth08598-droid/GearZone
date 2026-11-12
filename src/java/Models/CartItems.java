package Models;

import java.util.Date;

public class CartItems {

    private Integer cartId;
    private Integer userId;
    private Integer partId;
    private Integer quantity;
    private Date addedAt;
    private String status;

    public CartItems() {
    }

    public CartItems(Integer cartId, Integer userId, Integer partId, Integer quantity, Date addedAt, String status) {
        this.cartId = cartId;
        this.userId = userId;
        this.partId = partId;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.status = status;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
