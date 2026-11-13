package model;


public class GameFpsBenchmarks {

    private Integer benchmarkId;
    private Integer gameId;
    private Integer vgaPartId;
    private String resolution;
    private String graphicsPreset;
    private Integer avgFps;
    private String status;

    public GameFpsBenchmarks() {}

    public Integer getBenchmarkId() { return benchmarkId; }
    public void setBenchmarkId(Integer benchmarkId) { this.benchmarkId = benchmarkId; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public Integer getVgaPartId() { return vgaPartId; }
    public void setVgaPartId(Integer vgaPartId) { this.vgaPartId = vgaPartId; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getGraphicsPreset() { return graphicsPreset; }
    public void setGraphicsPreset(String graphicsPreset) { this.graphicsPreset = graphicsPreset; }

    public Integer getAvgFps() { return avgFps; }
    public void setAvgFps(Integer avgFps) { this.avgFps = avgFps; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
