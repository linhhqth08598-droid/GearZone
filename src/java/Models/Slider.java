package Models;

public class Slider {
    private int sliderId;
    private String title;
    private String imageBase64;

    public int getSliderId() { return sliderId; }
    public void setSliderId(int sliderId) { this.sliderId = sliderId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}