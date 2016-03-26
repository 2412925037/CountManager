package com.tower.countmanager.bean;

public class HomeTodoTaskBean {

    private String workItemId;
    private String title;
    private String typeName;
    private String sendEmpName;
    private String createTime;

    public String getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getSendEmpName() {
        return sendEmpName;
    }

    public void setSendEmpName(String sendEmpName) {
        this.sendEmpName = sendEmpName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
