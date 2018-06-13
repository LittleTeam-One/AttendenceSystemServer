package com.example.mrc.attendencesystem.entity;

import java.util.ArrayList;

public class TranObject {
    TranObjectType type;//��Ϣ����
    private String fromUser;// �����ĸ��û�
    private String toUser;// �����ĸ��û�
    private User user;// ������û���������������ǿ����Զ����κ�
    private Group group;//����������
    private ArrayList<Group> groupList;//��������б����
    private  boolean isSuccess; //�����Ƿ�ɹ�
    private Message message;//�������Ϣ����
    private GroupSignInMessage signInfo;//ǩ����Ϣ
    private ArrayList<GroupSignInMessage> signInfoslist;//ǩ����¼��Ϣ����
    private ArrayList<Message> groupMessages;//��Ϣ����
    public ArrayList<Message> getMessages() {
        return groupMessages;
    }
    public void setMessages(ArrayList<Message> messages) {
        this.groupMessages = messages;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public TranObject() {

    }
    public TranObject(TranObjectType type) {
        this.type = type;
    }
    private GroupRequest request;//Ⱥ������
    private ArrayList<GroupMessage> groupMessageArrayList;//��ȡȺ��Ϣ
    private GroupMessage sendGroupMessage;//����Ⱥ��Ϣ
    private ArrayList<UnReceivedMessage> unReceivedMessages;

    public ArrayList<UnReceivedMessage> getUnReceivedMessages() {
        return unReceivedMessages;
    }

    public void setUnReceivedMessages(ArrayList<UnReceivedMessage> unReceivedMessages) {
        this.unReceivedMessages = unReceivedMessages;
    }

    public GroupMessage getSendGroupMessage() {
        return sendGroupMessage;
    }

    public void setSendGroupMessage(GroupMessage sendGroupMessage) {
        this.sendGroupMessage = sendGroupMessage;
    }

    public ArrayList<GroupMessage> getGroupMessageArrayList() {
        return groupMessageArrayList;
    }

    public void setGroupMessageArrayList(ArrayList<GroupMessage> groupMessageArrayList) {
        this.groupMessageArrayList = groupMessageArrayList;
    }

    public GroupRequest getRequest() {
        return request;
    }

    public void setRequest(GroupRequest request) {
        this.request = request;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }


    public TranObjectType getType() {
        return type;
    }
    public void setGroup(Group group2) {
        this.group = group2;
    }
    public void setGroupList(ArrayList<Group> groupsList) {
        this.groupList = groupsList;
    }
    public Group getGroup() {
        return group;
    }
    public String getFromUser() {
        return fromUser;
    }
    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }
    public String getToUser() {
        return toUser;
    }
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
    public ArrayList<Group> getGroupList() {
        return groupList;
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message message) {
        this.message = message;
    }
    public GroupSignInMessage getSignInfo() {
        return signInfo;
    }
    public void setSignInfo(GroupSignInMessage signInfo) {
        this.signInfo = signInfo;
    }
    public ArrayList<GroupSignInMessage> getSignInfoslist() {
        return signInfoslist;
    }
    public void setSignInfoslist(ArrayList<GroupSignInMessage> signInfos) {
        this.signInfoslist = signInfos;
    }
}
