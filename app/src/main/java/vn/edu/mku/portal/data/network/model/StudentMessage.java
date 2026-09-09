package vn.edu.mku.portal.data.network.model;

import com.google.gson.annotations.SerializedName;

public class StudentMessage {

    @SerializedName("MessageID")
    private long messageId;

    @SerializedName("MessageSubject")
    private String messageSubject;

    @SerializedName("MessageBody")
    private String messageBody;

    @SerializedName("CreationDate")
    private String creationDate;

    @SerializedName("SenderID")
    private String senderId;

    @SerializedName("SenderName")
    private String senderName;

    @SerializedName("IsRead")
    private int isRead;

    @SerializedName("countIsRead")
    private int countIsRead;

    public long getMessageId() { return messageId; }
    public String getMessageSubject() { return messageSubject; }
    public String getMessageBody() { return messageBody; }
    public String getCreationDate() { return creationDate; }
    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public int getIsRead() { return isRead; }
    public int getCountIsRead() { return countIsRead; }
}