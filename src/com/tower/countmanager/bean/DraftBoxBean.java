package com.tower.countmanager.bean;
/**
 * 草稿箱实体类
 * @author WUSONG
 *
 */
public class DraftBoxBean {
private String task_type_name;
private String task_type2;
private String operator;
private String operate_time;
private String task_sno;
private String task_name;
private String task_kind;


public String getTask_kind() {
	return task_kind;
}
public void setTask_kind(String task_kind) {
	this.task_kind = task_kind;
}
public String getTask_type_name() {
	return task_type_name;
}
public void setTask_type_name(String task_type_name) {
	this.task_type_name = task_type_name;
}
public String getTask_type2() {
	return task_type2;
}
public void setTask_type2(String task_type2) {
	this.task_type2 = task_type2;
}
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}
public String getOperate_time() {
	return operate_time;
}
public void setOperate_time(String operate_time) {
	this.operate_time = operate_time;
}
public String getTask_sno() {
	return task_sno;
}
public void setTask_sno(String task_sno) {
	this.task_sno = task_sno;
}
public String getTask_name() {
	return task_name;
}
public void setTask_name(String task_name) {
	this.task_name = task_name;
}

}

