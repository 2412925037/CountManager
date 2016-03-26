package com.tower.countmanager.cnst;

public class Const {

	//SharedPreferences
	public static final String SHARED_PREF = "tower_prefs";
	public static final String USER_ID = "userId";//用户登录账号
    public static final String USER_PHOTO = "userPhoto";//用户头像
    public static final String TOKEN = "token";//令牌唯一标识
    public static final String ROLE_ID = "roleId";//令牌唯一标识

	//DB数据 
	public static final String TOWER_DB = "tower.db";

    //内网服务器
    //private static final String SERVER_URL = "http://10.4.123.131:8989/tower/";
    //生产环境
    private static final String SERVER_URL = "http://123.126.34.173:7081/cmms/";

	public static final String LOGIN_URL = SERVER_URL + "loginAction/login";//2.	登录接口
    public static final String USER_INFO_URL = SERVER_URL + "interfaceWorkFlowAction/getEmpInfo";//3.	人员基本信息接口
    public static final String HOME_URL = SERVER_URL + "portalAction/getPortal";//4.	区域经理/县域经理 首页接口
    public static final String PROJECT_LIST_URL = SERVER_URL + "interfaceWorkFlowAction/getInterfaceTaskList";//5.	待签收/待办/已办 查询接口
    public static final String SIGN_TASK_URL = SERVER_URL + "interfaceWorkFlowAction/doTaskSign";//7.	待签收列表 任务签收接口
    public static final String WORKER_TODO_COMMIT_URL = SERVER_URL + "workflow/doCommitTask";//9.	县域经理提交接口
    public static final String GET_TASKN_CODE_URL = SERVER_URL + "appTaskInfoAction/doOpenTaskStartN";//	县域经理新增汇报任务打开页面后调用的接口

    public static final String GET_TASK_CODE_URL = SERVER_URL + "appTaskInfoAction/doOpenTaskStartT";//区域经理新增下达任务打开页面后调用的接口
    public static final String GET_TASK_RECIEVE_PERSON_URL = SERVER_URL + "appTaskInfoAction/getReceiveUserList";//14.	获取任务接收人接口
    public static final String CREATE_SITE_INFO_URL = SERVER_URL + "appTaskInfoAction/doSaveNewSite";//11.	新增站点信息接口
    public static final String PROJECT_INFO_URL = SERVER_URL + "appTaskInfoAction/getFeedBackList";//20.	县域经理点击待办列表查询待办信息接口
    public static final String TODO_CREATE_CALLBACK_INFO_URL = SERVER_URL + "commonAction/getFeedbackInfoSeq";//11.	县域经理新增反馈接口
    public static final String TODO_CREATE_CALLBACK_URL = SERVER_URL + "appTaskInfoAction/doSaveFeedBackInfo";//24.	县域经理新增反馈保存接口
  	public static final String WAIT_READ_URL= SERVER_URL + "interfaceWorkFlowAction/getReadList";//待办-待阅-区域经理
  	public static final String GET_TASK_ASSESS_INFO_URL = SERVER_URL + "appTaskInfoAction/getTaskAssessInfo";//28.	区域经理待办详细信息接口
  	public static final String SAVE_NEW_ASSESS_URL = SERVER_URL + "appTaskInfoAction/doSaveNewAssess";//29.	区域经理待办办理接口（评价）
    public static final String UPLOAD_PHOTO_URL = SERVER_URL + "uploadForApp";//33.	照片上传接口
    public static final String GET_TASK_INFO_URL = SERVER_URL + "appTaskInfoAction/doOpenTaskInfo";//23.区域经理草稿详细信息接口(任务详情)
  	public static final String TODO_FEEDBACK_INFO_URL = SERVER_URL + "appTaskInfoAction/getFeedBackTask";//反馈页面信息查询接口
    public static final String TODO_FEEDBACK_COMMIT_URL = SERVER_URL + "interfaceWorkFlowAction/doUpdateFeedBack";//8.	区域经理确认反馈意见接口
    public static final String GET_TASK_REPORT_INFO_URL= SERVER_URL + "appTaskInfoAction/doOpenHBTaskInfo";//19.县域经理汇报的详细信息接口
  	//考勤查询
  	public static final String ATTENDANCE_QUERY_LIST = SERVER_URL + "appCheckTimeAction/getCheckTimeList";
  	public static final String ATTENDANCE_QUERY_DETAIL_LIST = SERVER_URL + "appCheckTimeAction/getCheckTimeByUser";
  	public static final String ATTENDANCE_QUERY_MANAGERT_LIST = SERVER_URL + "appCheckTimeAction/getSelfCheckTimeList";
    public static final String UPLOAD_LOCATION = SERVER_URL + "appCheckTimeAction/doSaveCheckTimeInfo";
    public static final String GET_VOICE_FILE_URL = SERVER_URL + "appTaskInfoAction/getVoiceList";
    public static final String UPLOAD_VOICE_FILE_URL = SERVER_URL + "uploadVoiceForApp";
  	
  	//任务查询
  	public static final String GET_TASK_LIST_T = SERVER_URL + "appTaskInfoAction/getTaskListT";
  	public static final String GET_TASK_LIST_N = SERVER_URL + "appTaskInfoAction/getTaskListN";
  	//草稿箱接口
	public static final String DRAFTBOX_LIST_URL= SERVER_URL + "appTaskInfoAction/getSelfTaskList";//草稿箱List列表查询-县域经理
	public static final String ADD_REPORT_URL2= SERVER_URL + "appTaskInfoAction/doTaskCommitN";//草稿箱-起草汇报-县域经理
	public static final String ADD_REPORT_URL1= SERVER_URL + "appTaskInfoAction/doTaskCommitT";//草稿箱-起草下达-区域经理
	public static final String DELETE_REPORT_URL= SERVER_URL + "appTaskInfoAction/doDelTask";//草稿箱-起草删除-县域经理
	public static final String DRAFTBOX_DETAIL_URL= SERVER_URL + "appTaskInfoAction/doOpenTaskInfo";//草稿箱-起草详情-区域经理
	
	public static final String ASSIGNMENT_TASK_URL= SERVER_URL + "appTaskInfoAction/doSaveNewTaskT";//下达任务-区域经理
	public static final String REPORT_TASK_URL= SERVER_URL + "appTaskInfoAction/doSaveNewTaskN";//汇报任务-县域经理
	public static final String GET_SITE_URL= SERVER_URL + "appTaskInfoAction/getSiteInfo";//获取站点
	public static final String GET_REPORT_CHART_INFO_URL= SERVER_URL + "appTaskInfoAction/getTaskCharInfo";//30.任务报表查询接口
	
    //访问服务器返回结果
    public static final int REQUEST_SERVER_SUCCESS = 01;
    public static final int REQUEST_SERVER_FAILURE = 00;
    
    // 单次最多发送图片数
 	public static final int MAX_IMAGE_SIZE = 8;
    // listvew获取一次的数据量
    public static final int MAX_DATA_LIMIT = 20;
    //拍照
    public static final int TAKE_PICTURE = 03;
 	//相册中图片对象集合
 	public static final String EXTRA_IMAGE_LIST = "image_list";
 	//图片编辑调用类型
 	public static final String EXTRA_IMAGE_CALL_FLAG = "select_image_call_flag";
 	//当前选择的照片位置
 	public static final String EXTRA_CURRENT_IMG_POSITION = "current_img_position";
 	//是否显示删除按钮
 	public static final String IS_SHOW_DEL_BTN = "is_show_del_btn";
 	//代办工序进入子节
 	public static final int REQUEST_CODE_WORK_TODO_NOTE = 01;
 	
 	
}
