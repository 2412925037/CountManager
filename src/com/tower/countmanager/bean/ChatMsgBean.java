package com.tower.countmanager.bean;

public class ChatMsgBean {

	private String side;
    private String docPath;
    private String empName;
    private String user_img;
    private int second_length;
    private String uploadDate;

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getDocPath() {
        return docPath;
    }

    public void setDocPath(String docPath) {
        this.docPath = docPath;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getUser_img() {
        return user_img;
    }

    public void setUser_img(String user_img) {
        this.user_img = user_img;
    }

    public int getSecond_length() {
        return second_length;
    }

    public void setSecond_length(int second_length) {
        this.second_length = second_length;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }
}
