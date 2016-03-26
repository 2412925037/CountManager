package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.ProjectInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.XCRoundImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务处理与评价(区域经理)
 */
public class TaskProcessingEvaluationActvity extends Activity {

	private static final String TAG = "TaskProcessingEvaluationActvity";

	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;
	@ViewInject(R.id.task_processing_evaluation_ratingbar)
	RatingBar ratingBar;
	@ViewInject(R.id.table1)
	TableLayout table;
	@ViewInject(R.id.table2)
	TableLayout tableOver3Star;

	@ViewInject(R.id.task_processing_evaluation_input_opinion)
	private EditText optionInput;
	@ViewInject(R.id.layout_btn_task_processing_evaluation)
	private RelativeLayout layoutBtn;

	@ViewInject(R.id.task_processing_person_name)
	private TextView personName;
	@ViewInject(R.id.task_processing_address)
	private TextView address;
	@ViewInject(R.id.task_processing_count_num)
	private TextView countNum;

	@ViewInject(R.id.task_processing_evaluation_task_name)
	private TextView taskName;
	@ViewInject(R.id.task_processing_evaluation_task_code)
	private TextView taskCode;
	@ViewInject(R.id.task_processing_evaluation_task_type)
	private TextView taskType;
	@ViewInject(R.id.avatar)
	XCRoundImageView avatar;
	@ViewInject(R.id.checkBox1)
	private CheckBox checkBox1;
	@ViewInject(R.id.checkBox2)
	private CheckBox checkBox2;
	@ViewInject(R.id.checkBox3)
	private CheckBox checkBox3;
	@ViewInject(R.id.checkBox4)
	private CheckBox checkBox4;
	@ViewInject(R.id.checkBox5)
	private CheckBox checkBox5;
	@ViewInject(R.id.checkBox6)
	private CheckBox checkBox6;
	@ViewInject(R.id.checkBox7)
	private CheckBox checkBox7;
	@ViewInject(R.id.checkBox8)
	private CheckBox checkBox8;

	Map<String, CheckBox> checkBoxMap = new HashMap<String, CheckBox>();
	Map<String, CheckBox> checkBoxMapOver3Star = new HashMap<String, CheckBox>();

	String taskKind = "";
	ProjectInfoBean pBean;
	String task_sno = "";
	String workId = "";
	String type = "";
	private ProgressDialog dialog = null;
	private boolean taskInfoNotClick = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_processing_evaluation);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		if("confirming".equals(getIntent().getStringExtra("task"))){
			subviewTitle.setText("待确认");
		}else{
		subviewTitle.setText(R.string.task_processing_evaluation_title);
		}
		titleLeftImg.setVisibility(View.VISIBLE);
		ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				if (arg1 < 4.0) {
					table.setVisibility(View.VISIBLE);
					tableOver3Star.setVisibility(View.GONE);
				} else {
					table.setVisibility(View.GONE);
					tableOver3Star.setVisibility(View.VISIBLE);
				}
			}
		});
		initCheckBoxValueMap();
		workId = getIntent().getStringExtra("workId");
		task_sno = getIntent().getStringExtra("task_sno");
		type = getIntent().getStringExtra("type");
		taskInfoNotClick = getIntent().getBooleanExtra("taskInfoNotClick", false);
		loadProjectInfo();
	}

	private void initCheckBoxValueMap() {
		checkBoxMap.put("C1", checkBox1);
		checkBoxMap.put("C2", checkBox2);
		checkBoxMap.put("C3", checkBox3);
		checkBoxMap.put("C4", checkBox4);
		checkBoxMapOver3Star.put("C5", checkBox5);
		checkBoxMapOver3Star.put("C6", checkBox6);
		checkBoxMapOver3Star.put("C7", checkBox7);
		checkBoxMapOver3Star.put("C8", checkBox8);
	}

	@OnClick(value = { R.id.subview_title_arrow, R.id.btn_phone, R.id.btn_commit, R.id.layout_task_info,
			R.id.voice_message })
	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.subview_title_arrow:
			finish();
			break;
		case R.id.btn_commit:
			if (!Utils.isConnect(this)) {
				Toast.makeText(this, R.string.app_connecting_network_no_connect, Toast.LENGTH_SHORT).show();
			} else {
				commitButton();
			}
			break;
		case R.id.btn_phone:
			makePhoneCall();
			break;
		case R.id.layout_task_info:
			//判断任务详情是否可以点击
			if(!taskInfoNotClick){
				// 区分任务详情（N上报，T下达）
				if ("T".equalsIgnoreCase(taskKind) || "finish".equalsIgnoreCase(type)) {
					startActivity(new Intent(this, TaskWaitReadActivity.class).putExtra("workItemId", workId).putExtra(
							"type", "finish").putExtra("task_sno", pBean.getTask_sno()));
				} else {
					startActivity(new Intent(this, ReportTaskFinishActivity.class).putExtra("workId", workId).putExtra("task_sno", pBean.getTask_sno()));
				}
			}
			break;
		case R.id.voice_message:
			if ("finish".equalsIgnoreCase(type)) {
				startActivity(new Intent(this, VoiceDetailActivity.class).putExtra("taskSno", pBean.getTask_sno()));
			}else {
				startActivity(new Intent(this, VoiceActivity.class).putExtra("task_sno", pBean.getTask_sno()));
			}
			break;
		}
	}

	private void makePhoneCall() {
		String phoneNum = pBean.getPhone_num();
		if (!TextUtils.isEmpty(phoneNum)) {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
			startActivity(intent);
		}

	}

	/**
	 * 拉取详细信息
	 */
	private void loadProjectInfo() {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		if(TextUtils.isEmpty(workId)) {
            map.put("workItemId", "");
            map.put("task_sno", task_sno);
        } else {
            map.put("workItemId", workId);
            map.put("task_sno", "");
        }
		
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.GET_TASK_ASSESS_INFO_URL, handlerLoad, Utils.getToken(this));
	}

	private List<String> getCheckedAssessCodes(Map<String, CheckBox> map) {
		List<String> checkedList = new ArrayList<String>();
		for (String key : map.keySet()) {
			CheckBox checkBox = map.get(key);
			if (checkBox.isChecked()) {
				checkedList.add(key);
			}
		}
		return checkedList;
	}

	private void setCheckedAssessCodes(String assess_codes, Map<String, CheckBox> map) {
		// 服务端数据包含空格
		String[] ids = assess_codes.replaceAll(" ", "").split(",");
		for (int i = 0; i < ids.length; i++) {
			String key = ids[i];
			if (map.containsKey(key)) {
				CheckBox checkBox = map.get(key);
				checkBox.setChecked(true);
			}
		}
	}

	/**
	 * 设置所有checkBox不可点击
	 * 
	 * @param map
	 */
	private void setCheckBoxClicbleFalse(Map<String, CheckBox> map) {
		for (String key : map.keySet()) {
			CheckBox checkBox = map.get(key);
			checkBox.setEnabled(false);
		}
	}

	/**
	 * 提交
	 */
	private void commitButton() {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workItemId", workId);
		map.put("task_sno", pBean.getTask_sno());
		map.put("emp_id", pBean.getSign_user());
		String assess_codes = "";
		int i = (int) ratingBar.getRating();
		String s1 = "";
		if (i > 3) {
			s1 = getCheckedAssessCodes(checkBoxMapOver3Star).toString();
		} else {
			s1 = getCheckedAssessCodes(checkBoxMap).toString();
		}
		assess_codes = s1.substring(1, s1.length() - 1);
		map.put("assess_codes", assess_codes);
		map.put("score", String.valueOf(i));
		map.put("opinion", optionInput.getText().toString().trim());
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.SAVE_NEW_ASSESS_URL, handlerCommit, Utils.getToken(this));
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}

	private Handler handlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (dialog != null)
				dialog.dismiss();
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(TaskProcessingEvaluationActvity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						String result = jsonObject.getString("result");
						Gson gson = new Gson();
						pBean = gson.fromJson(result, ProjectInfoBean.class);
						personName.setText(pBean.getSign_user_name());
						address.setText(pBean.getProvince() + pBean.getCity() + pBean.getDistrict());
						countNum.setText(String.format(getString(R.string.task_processing_evaluation_count_num),
								pBean.getCount_num()));
						taskName.setText(pBean.getTask_name());
						taskCode.setText(pBean.getTask_code());
						taskType.setText(pBean.getTask_type_name());
						
						taskKind = pBean.getTask_kind();
						// 头像
						String avatarUrl = pBean.getUserImg();
						if (!TextUtils.isEmpty(avatarUrl)) {
							AsyncImageLoader loader = new AsyncImageLoader(avatar);
							loader.execute(avatarUrl);
						}
						if ("finish".equals(type)) {
							//已办不可点击
							setCheckBoxClicbleFalse(checkBoxMap);
							setCheckBoxClicbleFalse(checkBoxMapOver3Star);
							if (!TextUtils.isEmpty(pBean.getScore())) {
								int i = Integer.parseInt(pBean.getScore());
								ratingBar.setProgress(i);
								String assess_codes = pBean.getAssess_codes();
								if (i <= 3) {
									setCheckedAssessCodes(assess_codes, checkBoxMap);
//									setCheckBoxClicbleFalse(checkBoxMap);
									table.setVisibility(View.VISIBLE);
								} else {
									setCheckedAssessCodes(assess_codes, checkBoxMapOver3Star);
//									setCheckBoxClicbleFalse(checkBoxMapOver3Star);
									tableOver3Star.setVisibility(View.VISIBLE);
								}
							} else {
								table.setVisibility(View.VISIBLE);
								ratingBar.setProgress(0);
							}
							ratingBar.setIsIndicator(true);
							optionInput.setText(pBean.getOpinion());
							optionInput.setKeyListener(null);
							layoutBtn.setVisibility(View.GONE);
						} else {
							table.setVisibility(View.VISIBLE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(TaskProcessingEvaluationActvity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	private Handler handlerCommit = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (dialog != null)
				dialog.dismiss();
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					Toast.makeText(TaskProcessingEvaluationActvity.this, jsonObject.getString("resultdesc"),
							Toast.LENGTH_SHORT).show();
					if ("01".equals(success)) {
						TaskProcessingEvaluationActvity.this.finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(TaskProcessingEvaluationActvity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

}
