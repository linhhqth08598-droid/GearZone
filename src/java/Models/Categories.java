package Models;


public class Categories {

    private Integer categoryId;
    private String categoryCode;
    private String categoryName;
    private String description;
    private Boolean isRequiredInBuild;
    private String status;

    public Categories() {
    }

    public Categories(Integer categoryId, String categoryCode, String categoryName, String description, Boolean isRequiredInBuild, String status) {
        this.categoryId = categoryId;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.description = description;
        this.isRequiredInBuild = isRequiredInBuild;
        this.status = status;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsRequiredInBuild() {
        return isRequiredInBuild;
    }

    public void setIsRequiredInBuild(Boolean isRequiredInBuild) {
        this.isRequiredInBuild = isRequiredInBuild;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
