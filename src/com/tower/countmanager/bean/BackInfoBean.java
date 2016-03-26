package com.tower.countmanager.bean;

import java.util.List;

public class BackInfoBean {

	private String address;
	private String address_remark;
	private String complete_desc;
	private String latitude;
	private String longitude;
    private String task_sno;
    private List<ImageBean> imgList;
    private String re_feedback_info;

    
    public String getRe_feedback_info() {
		return re_feedback_info;
	}

	public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress_remark() {
        return address_remark;
    }

    public void setAddress_remark(String address_remark) {
        this.address_remark = address_remark;
    }

    public String getComplete_desc() {
        return complete_desc;
    }

    public void setComplete_desc(String complete_desc) {
        this.complete_desc = complete_desc;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public List<ImageBean> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImageBean> imgList) {
        this.imgList = imgList;
    }

    public String getTask_sno() {
        return task_sno;
    }

    public void setTask_sno(String task_sno) {
        this.task_sno = task_sno;
    }
}
