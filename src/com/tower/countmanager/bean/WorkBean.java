package com.tower.countmanager.bean;

/**
 * WorkFragment.java
 */
public class WorkBean {

	private String workItemId;
    private String title;
    private String typeName;
    private String empName;
    private String createTime;
    private String procInstId;
    private String taskSno;
    private String confirmNum;
    public String getWorkItemId() {
        return workItemId;
    }

    public void setWorkItemId(String workItemId) {
        this.workItemId = workItemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

	public String getProcInstId() {
		return procInstId;
	}

	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}

	public String getTaskSno() {
		return taskSno;
	}

	public void setTaskSno(String taskSno) {
		this.taskSno = taskSno;
	}

	public String getConfirmNum() {
		return confirmNum;
	}

	public void setConfirmNum(String confirmNum) {
		this.confirmNum = confirmNum;
	}
    
}
