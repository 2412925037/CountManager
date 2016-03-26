package com.tower.countmanager.bean;

public class HomeManagerBean {

	private HomeTodoTaskBean taskInfo;
    private String taskTotal;
    private String countyPersonTotal;
    private String completePrjTotal;
    private String completePleased;
    private String prjTotal;
    private String month;
    private String onlinePersonNum;

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

    public String getCountyPersonTotal() {
        return countyPersonTotal;
    }

    public void setCountyPersonTotal(String countyPersonTotal) {
        this.countyPersonTotal = countyPersonTotal;
    }

    public String getCompletePrjTotal() {
        return completePrjTotal;
    }

    public void setCompletePrjTotal(String completePrjTotal) {
        this.completePrjTotal = completePrjTotal;
    }

    public String getCompletePleased() {
        return completePleased;
    }

    public void setCompletePleased(String completePleased) {
        this.completePleased = completePleased;
    }

    public String getPrjTotal() {
        return prjTotal;
    }

    public void setPrjTotal(String prjTotal) {
        this.prjTotal = prjTotal;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getOnlinePersonNum() {
        return onlinePersonNum;
    }

    public void setOnlinePersonNum(String onlinePersonNum) {
        this.onlinePersonNum = onlinePersonNum;
    }
}
