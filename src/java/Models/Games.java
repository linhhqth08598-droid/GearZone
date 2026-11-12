package Models;


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
    private Boolean isActive;

    public Games() {
    }

    public Games(Integer gameId, String gameName, String genre, String coverUrl, Integer minRamGb, Integer recommendedRamGb, Integer minCpuScore, Integer recommendedCpuScore, Integer minGpuScore, Integer recommendedGpuScore, Boolean isActive) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.genre = genre;
        this.coverUrl = coverUrl;
        this.minRamGb = minRamGb;
        this.recommendedRamGb = recommendedRamGb;
        this.minCpuScore = minCpuScore;
        this.recommendedCpuScore = recommendedCpuScore;
        this.minGpuScore = minGpuScore;
        this.recommendedGpuScore = recommendedGpuScore;
        this.isActive = isActive;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getMinRamGb() {
        return minRamGb;
    }

    public void setMinRamGb(Integer minRamGb) {
        this.minRamGb = minRamGb;
    }

    public Integer getRecommendedRamGb() {
        return recommendedRamGb;
    }

    public void setRecommendedRamGb(Integer recommendedRamGb) {
        this.recommendedRamGb = recommendedRamGb;
    }

    public Integer getMinCpuScore() {
        return minCpuScore;
    }

    public void setMinCpuScore(Integer minCpuScore) {
        this.minCpuScore = minCpuScore;
    }

    public Integer getRecommendedCpuScore() {
        return recommendedCpuScore;
    }

    public void setRecommendedCpuScore(Integer recommendedCpuScore) {
        this.recommendedCpuScore = recommendedCpuScore;
    }

    public Integer getMinGpuScore() {
        return minGpuScore;
    }

    public void setMinGpuScore(Integer minGpuScore) {
        this.minGpuScore = minGpuScore;
    }

    public Integer getRecommendedGpuScore() {
        return recommendedGpuScore;
    }

    public void setRecommendedGpuScore(Integer recommendedGpuScore) {
        this.recommendedGpuScore = recommendedGpuScore;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

}
