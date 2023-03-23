package voynap.InterviewTask.dto;


import java.util.Date;
import java.util.List;


public class NotificationDTO {
    private String head;
    private Date date;
    private String text;

    private List<Integer> customersId;

    private boolean sendToAll;

    public NotificationDTO() {
        this.date = new Date();
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Integer> getCustomersId() {
        return customersId;
    }

    public void setCustomersId(List<Integer> customersId) {
        this.customersId = customersId;
    }

    public boolean isSendToAll() {
        return sendToAll;
    }

    public void setSendToAll(boolean sendToAll) {
        this.sendToAll = sendToAll;
    }
}
