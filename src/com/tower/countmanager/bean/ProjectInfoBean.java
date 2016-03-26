package com.tower.countmanager.bean;

public class ProjectInfoBean {
	String task_sno; //任务流水
	String task_code; // 任务编码
	String task_name; // 任务名称
	String task_type_code; // 任务类型编码
	String task_type_name; // 任务类型名称
	String task_type2; // 二级任务类型
	String task_info; // 任务内容说明
	String score; // 评价分
	String assess_id; // 评价勾选项 //1-3分依次为C1/C2/C3/C4 4-5分依次为C5/C6/C7/C8
	String opinion; // 评分意见
	String sign_user; // 被评价人员id
	String sign_user_name; // 被评价人员name
	String phone_num; // 电话号码
	String district; // 区县
	String city; // 城市
	String province; // 省份
	String finish_time; // 任务完成时间
	String count_num; // 本月任务完成数
	String userImg; // 头像
	String assess_codes; // 评分勾选项
	String task_kind; 
	
	
	public String getTask_kind() {
		return task_kind;
	}
	public String getAssess_codes() {
		return assess_codes;
	}
	public String getTask_sno() {
		return task_sno;
	}
	public String getSign_user_name() {
		return sign_user_name;
	}
	public String getTask_code() {
		return task_code;
	}
	public String getTask_name() {
		return task_name;
	}
	public String getTask_type_code() {
		return task_type_code;
	}
	public String getTask_type_name() {
		return task_type_name;
	}
	public String getTask_type2() {
		return task_type2;
	}
	public String getTask_info() {
		return task_info;
	}
	public String getScore() {
		return score;
	}
	public String getAssess_id() {
		return assess_id;
	}
	public String getOpinion() {
		return opinion;
	}
	public String getSign_user() {
		return sign_user;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public String getDistrict() {
		return district;
	}
	public String getCity() {
		return city;
	}
	public String getProvince() {
		return province;
	}
	public String getFinish_time() {
		return finish_time;
	}
	public String getCount_num() {
		return count_num;
	}
	public String getUserImg() {
		return userImg;
	}

}
