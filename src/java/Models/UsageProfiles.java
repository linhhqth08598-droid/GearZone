package Models;


public class UsageProfiles {

    private Integer profileId;
    private String profileName;
    private String targetGameList;
    private String targetResolution;
    private String targetFps;
    private String status;

    public UsageProfiles() {
    }

    public UsageProfiles(Integer profileId, String profileName, String targetGameList, String targetResolution, String targetFps, String status) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.targetGameList = targetGameList;
        this.targetResolution = targetResolution;
        this.targetFps = targetFps;
        this.status = status;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getTargetGameList() {
        return targetGameList;
    }

    public void setTargetGameList(String targetGameList) {
        this.targetGameList = targetGameList;
    }

    public String getTargetResolution() {
        return targetResolution;
    }

    public void setTargetResolution(String targetResolution) {
        this.targetResolution = targetResolution;
    }

    public String getTargetFps() {
        return targetFps;
    }

    public void setTargetFps(String targetFps) {
        this.targetFps = targetFps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
