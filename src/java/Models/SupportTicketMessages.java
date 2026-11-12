package Models;

import java.util.Date;

public class SupportTicketMessages {

    private Integer messageId;
    private Integer ticketId;
    private Integer senderId;
    private String message;
    private String attachmentUrl;
    private Date createdAt;
    private String status;

    public SupportTicketMessages() {
    }

    public SupportTicketMessages(Integer messageId, Integer ticketId, Integer senderId, String message, String attachmentUrl, Date createdAt, String status) {
        this.messageId = messageId;
        this.ticketId = ticketId;
        this.senderId = senderId;
        this.message = message;
        this.attachmentUrl = attachmentUrl;
        this.createdAt = createdAt;
        this.status = status;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
