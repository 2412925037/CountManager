package com.tower.countmanager.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "user")
public class UserEntity extends EntityBase {

    @Column(column = "empId")
    private String empId;//登陆人ID
	@Column(column = "empName")
    private String empName;//登陆人姓名
    @Column(column = "empImg")
    private String empImg;//登陆人头像地址
    @Column(column = "dutyName")
    private String dutyName;//登陆人职务
	@Column(column = "companyId")
    private String companyId;//登陆人所属公司ID
    @Column(column = "companyName")
    private String companyName;//登陆人所属公司名称
    @Column(column = "orgId")
    private String orgId;//登陆人所属组织机构ID
    @Column(column = "orgName")
    private String orgName;//登陆人所属组织机构名称
    @Column(column = "provinceName")
    private String provinceName;
    @Column(column = "cityName")
    private String cityName;
    @Column(column = "districtName")
    private String districtName;//区县名称

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpImg() {
        return empImg;
    }

    public void setEmpImg(String empImg) {
        this.empImg = empImg;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    @Override
	public String toString() {
            return "User [empId=" + empId + ", empName=" + empName + ", companyId=" + companyId + ", companyName=" + companyName
                    + ", orgId=" + orgId + ", orgName=" + orgName + ", provinceName=" + provinceName +
                    ", cityName=" + cityName + ", districtName=" + districtName + "]";
	}
}
