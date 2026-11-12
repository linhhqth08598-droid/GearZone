package Models;


public class ShippingMethods {

    private Integer shippingMethodId;
    private String methodCode;
    private String methodName;
    private String carrierCode;
    private Double baseFee;
    private Integer estimatedDays;
    private String status;

    public ShippingMethods() {
    }

    public ShippingMethods(Integer shippingMethodId, String methodCode, String methodName, String carrierCode, Double baseFee, Integer estimatedDays, String status) {
        this.shippingMethodId = shippingMethodId;
        this.methodCode = methodCode;
        this.methodName = methodName;
        this.carrierCode = carrierCode;
        this.baseFee = baseFee;
        this.estimatedDays = estimatedDays;
        this.status = status;
    }

    public Integer getShippingMethodId() {
        return shippingMethodId;
    }

    public void setShippingMethodId(Integer shippingMethodId) {
        this.shippingMethodId = shippingMethodId;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getCarrierCode() {
        return carrierCode;
    }

    public void setCarrierCode(String carrierCode) {
        this.carrierCode = carrierCode;
    }

    public Double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(Double baseFee) {
        this.baseFee = baseFee;
    }

    public Integer getEstimatedDays() {
        return estimatedDays;
    }

    public void setEstimatedDays(Integer estimatedDays) {
        this.estimatedDays = estimatedDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
