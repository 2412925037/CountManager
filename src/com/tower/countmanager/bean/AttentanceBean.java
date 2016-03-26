package com.tower.countmanager.bean;

import java.util.List;

public class AttentanceBean {
	private String emp_id;
	private String emp_name;
	private String check_time;
	private String province;
	private String city;
	private String district;
	private String result;
	private String check_day;
	private String user_img;
	private List<CoordinateBean> coordinateList;

	public String getUser_img() {
		return user_img;
	}
	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public String getCheck_time() {
		return check_time;
	}
	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getCheck_day() {
		return check_day;
	}
	public void setCheck_day(String check_day) {
		this.check_day = check_day;
	}
	public List<CoordinateBean> getCoordinateList() {
		return coordinateList;
	}
	public void setCoordinateList(List<CoordinateBean> coordinateList) {
		this.coordinateList = coordinateList;
	}
}
