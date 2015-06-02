package bsu.famcs.chat.model;
import bsu.famcs.chat.util.MessageUtil;

public class Message {
    private final String id;
    private String userName;
    private String msgText;
    private String sendDate;
    private String changeDate;
    private boolean isDeleted;

    public Message(String id, String userName, String msgText, String sendDate,
                   String changeDate, boolean isDeleted) {
        this.id = id;
        this.userName = userName;
        this.msgText = msgText;
        this.sendDate = sendDate;
        this.changeDate = changeDate;
        this.isDeleted = isDeleted;
    }

    public void setChangeDate() {
        this.changeDate = MessageUtil.generateCurrentDate();
    }

    public void setMsgText(String msgText){
        this.msgText = msgText;
    }

    public void isDelete() {
        isDeleted = true;
        msgText = "isDeleted";
    }

    public String getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getMsgText() {
        return msgText;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"msgText\":\"").append(msgText)
                .append("\", \"userName\":\"").append(userName)
                .append("\", \"id\":\"").append(id)
                .append("\", \"sendDate\":\"").append(sendDate)
                .append("\", \"changeDate\":\"").append(changeDate)
                .append("\", \"isDeleted\":\"").append(isDeleted).append("\"}");
        return sb.toString();
    }

    public String getUserMessage() {
        StringBuilder sb = new StringBuilder(getSendDate());
        sb.append(' ')
                .append(userName)
                .append(" : ")
                .append(getMsgText());
        return sb.toString();
    }
}