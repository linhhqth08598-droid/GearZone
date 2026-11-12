package Models;


public class UserRequirementGames {

    private Integer requirementId;
    private Integer gameId;
    private String status;

    public UserRequirementGames() {
    }

    public UserRequirementGames(Integer requirementId, Integer gameId, String status) {
        this.requirementId = requirementId;
        this.gameId = gameId;
        this.status = status;
    }

    public Integer getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Integer requirementId) {
        this.requirementId = requirementId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
