package com.tower.countmanager.bean;

import java.util.List;

public class HomeWorkerBean {

	private HomeTodoTaskBean taskInfo;
    private String taskTotal;
    private String completeTaskTotal;
    private String receiveTaskTotal;
    private String taskPleased;
    private String pleasedRanking;
    private List<HomePleasedBean> pleasedList;

    public HomeTodoTaskBean getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(HomeTodoTaskBean taskInfo) {
        this.taskInfo = taskInfo;
    }

    public String getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(String taskTotal) {
        this.taskTotal = taskTotal;
    }

    public String getCompleteTaskTotal() {
        return completeTaskTotal;
    }

    public void setCompleteTaskTotal(String completeTaskTotal) {
        this.completeTaskTotal = completeTaskTotal;
    }

    public String getReceiveTaskTotal() {
        return receiveTaskTotal;
    }

    public void setReceiveTaskTotal(String receiveTaskTotal) {
        this.receiveTaskTotal = receiveTaskTotal;
    }

    public String getTaskPleased() {
        return taskPleased;
    }

    public void setTaskPleased(String taskPleased) {
        this.taskPleased = taskPleased;
    }

    public String getPleasedRanking() {
        return pleasedRanking;
    }

    public void setPleasedRanking(String pleasedRanking) {
        this.pleasedRanking = pleasedRanking;
    }

    public List<HomePleasedBean> getPleasedList() {
        return pleasedList;
    }

    public void setPleasedList(List<HomePleasedBean> pleasedList) {
        this.pleasedList = pleasedList;
    }
}
