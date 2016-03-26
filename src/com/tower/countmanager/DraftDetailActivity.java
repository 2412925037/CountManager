package com.tower.countmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.DateTimePickDialogUtil;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
/**
 * 下达任务 草稿详情
 * @author WUSONG
 *
 */
public class DraftDetailActivity extends Activity {
	private static final String TAG = "DraftDetailActivity";
	private Context mAppContext = null;
	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;
	@ViewInject(R.id.task_num)
	TextView taskNum;
	@ViewInject(R.id.task_recieve_person)
	TextView taskPerson;
	@ViewInject(R.id.task_name)
	TextView task_name;
	@ViewInject(R.id.task_type)
	TextView taskType;
	@ViewInject(R.id.task_info_emergency_degree)
	TextView taskEmergencyDegree;
	@ViewInject(R.id.task_info_multiple_feedback)
	TextView taskMultipleFeedback;
	@ViewInject(R.id.task_type2)
	EditText task_type2;
	@ViewInject(R.id.task_info_information)
	EditText task_info_information;
	@ViewInject(R.id.site_info_name)
	TextView siteInfoName;
	@ViewInject(R.id.site_info_location)
	TextView siteInfoLocation;
	@ViewInject(R.id.site_info_addr)
	TextView siteInfoAddr;
	@ViewInject(R.id.work_time)
	TextView work_time;
	@ViewInject(R.id.task_name_title)
	TextView task_name_title;
	@ViewInject(R.id.task_info_person_title)
	TextView task_info_person_title;
	@ViewInject(R.id.task_info_complete_time_title)
	TextView task_info_complete_time_title;
	@ViewInject(R.id.task_type_title)
	TextView task_type_title;
	@ViewInject(R.id.task_num_title)
	TextView task_num_title;
	@ViewInject(R.id.task_info_degree_title)
	TextView task_info_degree_title;
	@ViewInject(R.id.task_info_multiple_feedback_title)
	TextView task_info_multiple_feedback_title;
	private String[] taskTypes;
	private String[] emergencyDegree;
	private String[] multipleFeedback;
	private String[] taskRecievePersons;
	private String[] taskRecievePersonValues;
	int whichTaskType = 0;
	int whichEmergencyDegree = 0;
	int whichMultipleFeedback = 0;
	int whichTaskRecievePerson = 0;
	private String taskTypeNum="";//任务类型编码
	private String jinJiNum="";//紧急程度编码
	private String ifMore="";//是否多次反馈
	private String user_id="";
	String taskSno = "";//任务流水号
	private String type="";
	private String task_sno="";
	private String site_sno="";
	private String task_names;
	private String work_times;
	private String user_name="";
	List<String> name=new ArrayList<String>();
	List<String> id=new ArrayList<String>();
	boolean[] selected=new  boolean[100];  
	private static final int REQUEST_ACTIVITY_CREATE_SITE_INFO = 0x00011;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assignment_task);
        mAppContext = this.getApplicationContext();
		ViewUtils.inject(this);
		initViews();
	
		getMsg();
	}

	public void initViews() {
		for(int i=0;i<100;i++){
			selected[i]=false;
		}
		subviewTitle.setText(R.string.assignment_task_title);
		titleLeftImg.setVisibility(View.VISIBLE);
		taskTypes = getResources().getStringArray(R.array.task_type);
		task_name_title.setText(Html.fromHtml("任务名称"+"<font color=\"#db4537\"> *</font>"));
		task_info_person_title.setText(Html.fromHtml("任务接收人"+"<font color=\"#db4537\"> *</font>"));
		task_info_complete_time_title.setText(Html.fromHtml("任务完成时限"+"<font color=\"#db4537\"> *</font>"));
		task_type_title.setText(Html.fromHtml("任务类型"+"<font color=\"#db4537\"> *</font>"));
		task_num_title.setText(Html.fromHtml("任务编号"+"<font color=\"#db4537\"> *</font>"));
		task_info_degree_title.setText(Html.fromHtml("紧急程度"+"<font color=\"#db4537\"> *</font>"));
		task_info_multiple_feedback_title.setText(Html.fromHtml("是否多次反馈"+"<font color=\"#db4537\"> *</font>"));
		emergencyDegree = getResources().getStringArray(R.array.task_emergency_degree);
		multipleFeedback = getResources().getStringArray(R.array.task_multiple_feedback);
		loadTaskRecievePerson();
		
	}
	public void getMsg(){
		RequestServerUtils util = new RequestServerUtils();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("task_sno",getIntent().getStringExtra("task_sno"));
		map.put("workItemId", "");
		util.load2Server(map, Const.DRAFTBOX_DETAIL_URL, handlers, Utils.getToken(this));
	}
	
	private Handler handlers = new Handler() {
		public void handleMessage(android.os.Message msg) {
			  Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(DraftDetailActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							user_id=jsonObject2.getString("emp_ids");
							site_sno=jsonObject2.getString("site_sno");
							task_sno=jsonObject2.getString("task_sno");
							taskTypeNum= jsonObject2.getString("task_type_code");
							if("T1".equals(taskTypeNum)){
								taskType.setText("新建基站");
							}else if("T2".equals(taskTypeNum)){
								taskType.setText("存量改造");
							}else if("T3".equals(taskTypeNum)){
								taskType.setText("项目管理");
							}else if("T4".equals(taskTypeNum)){
								taskType.setText("其他");
							}
							taskNum.setText(jsonObject2.getString("task_code"));
							task_type2.setText(jsonObject2.getString("task_type2"));
							task_name.setText(jsonObject2.getString("task_name"));
							task_info_information.setText(jsonObject2.getString("task_info"));
							
							jinJiNum=jsonObject2.getString("urgency");
							if("1".equals(jinJiNum)){
								taskEmergencyDegree.setText("紧急");
							}else if("2".equals(jinJiNum)){
								taskEmergencyDegree.setText("一般");
							}
							ifMore=jsonObject2.getString("multi_feedback");
							if("1".equals(ifMore)){
								taskMultipleFeedback.setText("是");
							}else if("0".equals(ifMore)){
								taskMultipleFeedback.setText("否");
							}
			                siteInfoName.setText(jsonObject2.getString("site_name"));
			                
			                if(TextUtils.isEmpty(jsonObject2.getString("longitude"))&&TextUtils.isEmpty(jsonObject2.getString("latitude"))){
			                	if(TextUtils.isEmpty(site_sno)){
			                		
			                	}else{
				                	  siteInfoLocation.setText("经度:未知  纬度:未知");
			                	}
			                }else if(TextUtils.isEmpty(jsonObject2.getString("longitude"))){
				                siteInfoLocation.setText("纬度:"+jsonObject2.getString("latitude"));
			                }else if(TextUtils.isEmpty(jsonObject2.getString("latitude"))){
				                siteInfoLocation.setText("经度:"+jsonObject2.getString("longitude"));
			                }else{
				                siteInfoLocation.setText("经度:"+jsonObject2.getString("longitude")+"  纬度:"+jsonObject2.getString("latitude"));
			                } 
			                if(TextUtils.isEmpty(jsonObject2.getString("district"))){
			                	siteInfoAddr.setText("");
			                }else{
				                siteInfoAddr.setText("所属区:"+jsonObject2.getString("district"));
			                }
			                work_time.setText(jsonObject2.getString("time_limit"));
			                taskPerson.setText(jsonObject2.getString("emp_names"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(DraftDetailActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	//任务类型
	public void taskTypes(){
		if("新建基站".equals(taskType.getText().toString())){
			taskTypeNum="T1";
		}else if("存量改造".equals(taskType.getText().toString())){
			taskTypeNum="T2";
		}else if("项目管理".equals(taskType.getText().toString())){
			taskTypeNum="T3";
		}else if("其他".equals(taskType.getText().toString())){
			taskTypeNum="T4";
		}
		if("紧急".equals(taskEmergencyDegree.getText().toString())){
			jinJiNum="1";
		}else if("一般".equals(taskEmergencyDegree.getText().toString())){
			jinJiNum="2";
		}
		if("是".equals(taskMultipleFeedback.getText().toString())){
			ifMore="1";
		}else if("否".equals(taskMultipleFeedback.getText().toString())){
			ifMore="0";
		}
		
		
	}
	
	

	/**
	 * 拉取任务接收人
	 */
	private void loadTaskRecievePerson() {
		RequestServerUtils util = new RequestServerUtils();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("role_id", "2");
		map.put("start", "0");
		map.put("limit", "100");
		util.load2Server(map, Const.GET_TASK_RECIEVE_PERSON_URL, handlerLoadRecievePerson, Utils.getToken(this));
	}

	private Handler handlerLoadRecievePerson = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(DraftDetailActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						JSONArray jsonArray = jsonObject2.getJSONArray("rows");
						taskRecievePersons = new String[jsonArray.length()];
						taskRecievePersonValues = new String[jsonArray.length()];
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jobj = jsonArray.getJSONObject(i);
							taskRecievePersons[i] = jobj.getString("text");
							taskRecievePersonValues[i] = jobj.getString("value");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(DraftDetailActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
    private void temporarySave(String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("task_sno",task_sno);
        map.put("task_code",taskNum.getText().toString());
        map.put("task_name",task_names);
        map.put("task_type_code",taskTypeNum);
        map.put("task_type2",task_type2.getText().toString());
        map.put("task_info",task_info_information.getText().toString());
        if(TextUtils.isEmpty(site_sno)){
            map.put("site_sno",taskSno);
        }else{
        	 map.put("site_sno",site_sno);
        }
        map.put("task_state",type);
        map.put("multi_feedback",ifMore);
        map.put("emp_ids",user_id);
        map.put("urgency", jinJiNum);
        map.put("time_limit",work_times);
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.ASSIGNMENT_TASK_URL, saveSupport, Utils.getToken(mAppContext));
        
    }
	private Handler saveSupport = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					if ("01".equals(success)) {
						setResult(RESULT_OK);
						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
                Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};
	@OnClick(value = { R.id.subview_title_arrow, R.id.task_type, R.id.layout_emergency_degree,
			R.id.layout_multiple_feedback, R.id.layout_recieve_person,R.id.layout_site_info,
			R.id.btn_save,R.id.btn_commit,R.id.layout_time ,R.id.voice_message})
	public void onButtonClick(View v) {
		task_names=task_name.getText().toString();
		work_times=work_time.getText().toString();
		switch (v.getId()) {
		case R.id.subview_title_arrow:
			this.finish();
			break;
			
		case R.id.task_type://任务类型
			showTaskTypeDialog();
			break;
		case R.id.layout_emergency_degree://紧急程度
			showEmergencyDegreeDialog();
			
			break;
		case R.id.layout_multiple_feedback://是否多次反馈
			showMultipleFeedbackDialog();
			break;
			//
		case R.id.layout_recieve_person://任务接收人
			if (taskRecievePersons.length > 0) {
				System.out.println(taskRecievePersons.length);
				showTaskPersonDialog();
			}
			break;
		case R.id.layout_site_info://站址信息
		   	if(TextUtils.isEmpty(site_sno)){
    			startActivityForResult(new Intent(this, CreateSiteInfokMsgActivity.class).putExtra("site_sno", taskSno), REQUEST_ACTIVITY_CREATE_SITE_INFO);
        	}else{
			startActivityForResult(new Intent(this, CreateSiteInfokMsgActivity.class).putExtra("site_sno", site_sno), REQUEST_ACTIVITY_CREATE_SITE_INFO);
        	}
			break;
		case R.id.btn_save:
			type="1";
		    taskTypes();
			temporarySave(type);
			break;
		case R.id.btn_commit:
			type="2";
		    taskTypes();
//			if(TextUtils.isEmpty(task_type2.getText().toString())||TextUtils.isEmpty(taskNum.getText().toString())||
//					TextUtils.isEmpty(user_id) || TextUtils.isEmpty(task_names)|| TextUtils.isEmpty(work_times)|| TextUtils.isEmpty(taskTypeNum)
//					|| TextUtils.isEmpty(jinJiNum)|| TextUtils.isEmpty(ifMore)){
//	                Toast.makeText(mAppContext, R.string.app_commit_people_isempty, Toast.LENGTH_SHORT).show();
//				}else{
					temporarySave(type);
					//}
			break;
		case R.id.layout_time:
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
        	String 	startTime= work_time.getText().toString();
        	DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
        			DraftDetailActivity.this, startTime);
			dateTimePicKDialog.dateTimePicKDialog(work_time);
			break;
		 case R.id.voice_message :
             startActivity(new Intent(this, VoiceActivity.class).putExtra("task_sno", task_sno));
             break;
		}
	}

	public void showTaskTypeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(taskTypes, whichTaskType,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichTaskType = which;
						dialog.dismiss();
						taskType.setText(taskTypes[whichTaskType]);
					}
				});
		builder.create().show();
	}

	public void showEmergencyDegreeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(emergencyDegree,
				whichEmergencyDegree, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichEmergencyDegree = which;
						dialog.dismiss();
						taskEmergencyDegree.setText(emergencyDegree[whichEmergencyDegree]);
					}
				});
		builder.create().show();
	}

	public void showMultipleFeedbackDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(multipleFeedback,
				whichMultipleFeedback, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichMultipleFeedback = which;
						dialog.dismiss();
						taskMultipleFeedback.setText(multipleFeedback[whichMultipleFeedback]);
					}
				});
		builder.create().show();
	}

	public void showTaskPersonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setMultiChoiceItems(taskRecievePersons, selected,
				new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
						selected[which] = isChecked;
					}
				}).setPositiveButton(R.string.app_alert_button_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				dialog.dismiss();
				StringBuffer sBufferNames = new StringBuffer();
				StringBuffer sBufferValues = new StringBuffer();
				for (int j = 0; j < selected.length; j++) {
					if (selected[j] == true) {
						sBufferNames.append("," + taskRecievePersons[j]);
						sBufferValues.append("," + taskRecievePersonValues[j]);
					}
				}
				sBufferNames.delete(0, 1);
				sBufferValues.delete(0, 1);
				user_id = sBufferValues.toString();
				taskPerson.setText(sBufferNames.toString());
			}
		});
		builder.create().show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_ACTIVITY_CREATE_SITE_INFO:
				//TODO  取任务流水号拉取站点信息
			case 11 :
					taskSno = data.getStringExtra("taskSno");
	                siteInfoName.setText(data.getStringExtra("site_name"));
	                if(TextUtils.isEmpty(data.getStringExtra("longitude"))&&TextUtils.isEmpty(data.getStringExtra("latitude"))){
	                	  siteInfoLocation.setText("经度:未知  纬度:未知");
	                }else if(TextUtils.isEmpty(data.getStringExtra("longitude"))){
		                siteInfoLocation.setText("纬度:"+data.getStringExtra("latitude"));
	                }else if(TextUtils.isEmpty(data.getStringExtra("latitude"))){
		                siteInfoLocation.setText("经度:"+data.getStringExtra("longitude"));
	                }else{
		                siteInfoLocation.setText("经度:"+data.getStringExtra("longitude")+"  纬度:"+data.getStringExtra("latitude"));
	                } 
	                siteInfoAddr.setText("所属区:"+data.getStringExtra("district"));
	                Log.e(TAG, "taskSno: " + taskSno);
	                break;
			default:
				
				break;
			}
		}
	}
}
