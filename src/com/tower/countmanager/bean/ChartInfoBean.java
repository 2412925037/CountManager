package com.tower.countmanager.bean;

import java.util.List;

public class ChartInfoBean {
	String end_month;
	String org_name;
	String start_month;
	List<ChartMonthDataBean> monthDate;
	
	public String getEnd_month() {
		return end_month;
	}
	public String getOrg_name() {
		return org_name;
	}
	public String getStart_month() {
		return start_month;
	}
	public List<ChartMonthDataBean> getMonthDate() {
		return monthDate;
	}

}
