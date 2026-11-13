package fourSome.model;


public class UserRequirementGames {

    private Integer requirementId;
    private Integer gameId;
    private String status;

    public UserRequirementGames() {}

    public Integer getRequirementId() { return requirementId; }
    public void setRequirementId(Integer requirementId) { this.requirementId = requirementId; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

}
