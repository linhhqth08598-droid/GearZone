package Models;


public class ShippingAddresses {

    private Integer addressId;
    private Integer userId;
    private String recipientName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String detailedAddress;
    private Boolean isDefault;
    private String status;

    public ShippingAddresses() {
    }

    public ShippingAddresses(Integer addressId, Integer userId, String recipientName, String phone, String province, String district, String ward, String detailedAddress, Boolean isDefault, String status) {
        this.addressId = addressId;
        this.userId = userId;
        this.recipientName = recipientName;
        this.phone = phone;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.detailedAddress = detailedAddress;
        this.isDefault = isDefault;
        this.status = status;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
