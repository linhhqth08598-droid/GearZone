package Models;

public class ProductLine {
    private int lineId;
    private int brandId;
    private String lineName;
    private String description;

    public ProductLine() {}

    public ProductLine(int lineId, int brandId, String lineName, String description) {
        this.lineId = lineId;
        this.brandId = brandId;
        this.lineName = lineName;
        this.description = description;
    }

    public int getLineId() { return lineId; }
    public void setLineId(int lineId) { this.lineId = lineId; }

    public int getBrandId() { return brandId; }
    public void setBrandId(int brandId) { this.brandId = brandId; }

    public String getLineName() { return lineName; }
    public void setLineName(String lineName) { this.lineName = lineName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}