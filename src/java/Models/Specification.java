package Models;

public class Specification {
    private int specId;
    private int categoryId;
    private String specName;

    public Specification() {}

    public Specification(int specId, int categoryId, String specName) {
        this.specId = specId;
        this.categoryId = categoryId;
        this.specName = specName;
    }

    public int getSpecId() { return specId; }
    public void setSpecId(int specId) { this.specId = specId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getSpecName() { return specName; }
    public void setSpecName(String specName) { this.specName = specName; }
}