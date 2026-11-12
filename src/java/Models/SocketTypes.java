package Models;


public class SocketTypes {

    private Integer socketId;
    private String socketName;
    private String status;

    public SocketTypes() {
    }

    public SocketTypes(Integer socketId, String socketName, String status) {
        this.socketId = socketId;
        this.socketName = socketName;
        this.status = status;
    }

    public Integer getSocketId() {
        return socketId;
    }

    public void setSocketId(Integer socketId) {
        this.socketId = socketId;
    }

    public String getSocketName() {
        return socketName;
    }

    public void setSocketName(String socketName) {
        this.socketName = socketName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
