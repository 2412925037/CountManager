package com.tower.countmanager.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "manager_county")
public class ManagerCountyEntity extends EntityBase {

    @Column(column = "empNames")
    private String empNames;//管辖人员
	@Column(column = "district")
    private String district;//管辖区市县
    @Column(column = "empId")
    private String empId;//登陆人姓名


    public String getEmpNames() {
        return empNames;
    }

    public void setEmpNames(String empNames) {
        this.empNames = empNames;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    @Override
	public String toString() {
            return "ManagerCounty [empNames=" + empNames + ", district=" + district + ", empId=" + empId +  "]";
	}
}
