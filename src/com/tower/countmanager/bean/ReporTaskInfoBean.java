package com.tower.countmanager.bean;

import java.util.List;

public class ReporTaskInfoBean {
	// 任务完成说明
	private String feedback_complete_desc;
	private String task_code;
	private String task_name;
	private String task_info;
	private String emp_ids;
	private String emp_names;
	private String task_type_name;
	private String latitude;
	private String longitude;
	List<ImageBean> imgList;
	private String site_name;
	private String province;
	private String city;
	private String district;
    private String task_sno;
	
	private String task_type2;

	public String getTask_type2() {
		return task_type2;
	}

	public String getEmp_names() {
		return emp_names;
	}

	public String getSite_name() {
		return site_name;
	}

	public String getProvince() {
		return province;
	}

	public String getCity() {
		return city;
	}

	public String getDistrict() {
		return district;
	}

	public String getFeedback_complete_desc() {
		return feedback_complete_desc;
	}

	public String getTask_type_name() {
		return task_type_name;
	}

	public String getTask_code() {
		return task_code;
	}

	public String getTask_name() {
		return task_name;
	}

	public String getTask_info() {
		return task_info;
	}

	public String getEmp_ids() {
		return emp_ids;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public List<ImageBean> getImgList() {
		return imgList;
	}

    public String getTask_sno() {
        return task_sno;
    }

    public void setTask_sno(String task_sno) {
        this.task_sno = task_sno;
    }
}
