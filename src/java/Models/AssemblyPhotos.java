package Models;


public class AssemblyPhotos {

    private Integer photoId;
    private Integer assemblyId;
    private String photoUrl;
    private String status;

    public AssemblyPhotos() {
    }

    public AssemblyPhotos(Integer photoId, Integer assemblyId, String photoUrl, String status) {
        this.photoId = photoId;
        this.assemblyId = assemblyId;
        this.photoUrl = photoUrl;
        this.status = status;
    }

    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Integer photoId) {
        this.photoId = photoId;
    }

    public Integer getAssemblyId() {
        return assemblyId;
    }

    public void setAssemblyId(Integer assemblyId) {
        this.assemblyId = assemblyId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
