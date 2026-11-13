package model;


public class Games {

    private Integer gameId;
    private String gameName;
    private String genre;
    private String coverUrl;
    private Integer minRamGb;
    private Integer recommendedRamGb;
    private Integer minCpuScore;
    private Integer recommendedCpuScore;
    private Integer minGpuScore;
    private Integer recommendedGpuScore;
    private String status;

    public Games() {}

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public Integer getMinRamGb() { return minRamGb; }
    public void setMinRamGb(Integer minRamGb) { this.minRamGb = minRamGb; }

    public Integer getRecommendedRamGb() { return recommendedRamGb; }
    public void setRecommendedRamGb(Integer recommendedRamGb) { this.recommendedRamGb = recommendedRamGb; }

    public Integer getMinCpuScore() { return minCpuScore; }
    public void setMinCpuScore(Integer minCpuScore) { this.minCpuScore = minCpuScore; }

    public Integer getRecommendedCpuScore() { return recommendedCpuScore; }
    public void setRecommendedCpuScore(Integer recommendedCpuScore) { this.recommendedCpuScore = recommendedCpuScore; }

    public Integer getMinGpuScore() { return minGpuScore; }
    public void setMinGpuScore(Integer minGpuScore) { this.minGpuScore = minGpuScore; }

    public Integer getRecommendedGpuScore() { return recommendedGpuScore; }
    public void setRecommendedGpuScore(Integer recommendedGpuScore) { this.recommendedGpuScore = recommendedGpuScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
