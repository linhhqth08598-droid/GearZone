package Models;


public class ProductSeries {

    private Integer seriesId;
    private Integer brandId;
    private String seriesName;
    private String status;

    public ProductSeries() {
    }

    public ProductSeries(Integer seriesId, Integer brandId, String seriesName, String status) {
        this.seriesId = seriesId;
        this.brandId = brandId;
        this.seriesName = seriesName;
        this.status = status;
    }

    public Integer getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(Integer seriesId) {
        this.seriesId = seriesId;
    }

    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
