package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.adapter.ImagePublishAdapter;
import com.tower.countmanager.bean.ImageBean;
import com.tower.countmanager.bean.ImageItem;
import com.tower.countmanager.bean.ReporTaskInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.SGridView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportTaskFinishActivity extends Activity {

	private static final String TAG = "ReportTaskFinishActivity";

	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;

	@ViewInject(R.id.custom_process_info_gridview)
	SGridView mGridView;
	@ViewInject(R.id.btn_save)
	private Button btnSave;
	ImagePublishAdapter mAdapter;
	ProgressDialog progressDialog = null;
	String path = "";
	String newPath = "";// 增加水印照片
	String taskSno = "";// 任务流水号
	// public List<ImageItem> mDataList = new ArrayList<ImageItem>();
	private static final int REQUEST_ACTIVITY_IMAGE_ZOOM = 0x00013;

	String processId;
	String projectId;
	String provinceId;
	String currentType = null;
	ArrayList<ImageItem> mDataList = new ArrayList<ImageItem>();

	@ViewInject(R.id.task_type)
	TextView taskType;
	@ViewInject(R.id.task_name)
	TextView taskName;
	@ViewInject(R.id.task_num)
	TextView taskNum;
	@ViewInject(R.id.task_info_information)
	TextView task_info_information;
	@ViewInject(R.id.site_info_name)
	TextView siteInfoName;
	@ViewInject(R.id.site_info_location)
	TextView siteInfoLocation;
	@ViewInject(R.id.site_info_addr)
	TextView siteInfoAddr;
//	@ViewInject(R.id.task_recieve_person)
//	TextView taskPerson;
	@ViewInject(R.id.task_complete_info)
	TextView taskCompleteInfo;
	@ViewInject(R.id.task_type2)
	TextView task_type2;

	int whichTaskType = 0;

	String workId = "";
	private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_finish_task);
		ViewUtils.inject(this);
		initViews();
		initGridView();
	}

	private void initViews() {
		subviewTitle.setText(R.string.work_info);
		titleLeftImg.setVisibility(View.VISIBLE);
		workId = getIntent().getStringExtra("workId");
		taskSno = getIntent().getStringExtra("task_sno");
		loadFinishProjectInfo();
		
	}

	private void loadFinishProjectInfo() {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		if(TextUtils.isEmpty(workId)) {
            map.put("workItemId", "");
            map.put("task_sno", taskSno);
        } else {
            map.put("workItemId", workId);
            map.put("task_sno", "");
        }
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.GET_TASK_REPORT_INFO_URL, handlerLoad, Utils.getToken(this));
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}

	private void initGridView() {
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mAdapter = new ImagePublishAdapter(this, mDataList, false, Const.MAX_IMAGE_SIZE);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				List<String> pathList = new ArrayList<String>();
				for (ImageItem imageItem : mDataList) {
					pathList.add(imageItem.getDoc_path());
				}
				Intent intent = new Intent(ReportTaskFinishActivity.this, ImageZoomActivity.class);
				intent.putStringArrayListExtra(Const.EXTRA_IMAGE_LIST, (ArrayList<String>) pathList);
				intent.putExtra(Const.EXTRA_CURRENT_IMG_POSITION, position);
				intent.putExtra(Const.IS_SHOW_DEL_BTN, false);
				startActivityForResult(intent, REQUEST_ACTIVITY_IMAGE_ZOOM);
			}
		});
	}

	@OnClick(value = { R.id.subview_title_arrow, R.id.voice_message })
	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.subview_title_arrow:
			finish();
			break;
		case R.id.voice_message:
			startActivity(new Intent(this, VoiceDetailActivity.class).putExtra("taskSno", taskSno));
			break;
		}
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
						Toast.makeText(ReportTaskFinishActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						Gson gson = new Gson();
						ReporTaskInfoBean rBean = gson
								.fromJson(jsonObject.getString("result"), ReporTaskInfoBean.class);
						taskName.setText(rBean.getTask_name());
						taskNum.setText(rBean.getTask_code());
						taskType.setText(rBean.getTask_type_name());
						task_info_information.setText(rBean.getTask_info());
//						taskPerson.setText(rBean.getEmp_names());
						taskCompleteInfo.setText(rBean.getFeedback_complete_desc());
						siteInfoName.setText(rBean.getSite_name());
						if (!TextUtils.isEmpty(rBean.getLongitude()) || !TextUtils.isEmpty(rBean.getLatitude()) ) {
							siteInfoLocation.setText(String.format(getString(R.string.auto_operation_location_longitude),
									rBean.getLongitude())+" "
									+ String.format(getString(R.string.auto_operation_location_latitude),
											rBean.getLatitude()));
						}
						siteInfoAddr.setText(rBean.getProvince() + rBean.getCity() + rBean.getDistrict());
						task_type2.setText(rBean.getTask_type2());
						List<ImageBean> imgList = rBean.getImgList();
						for (int i = 0; i < imgList.size(); i++) {
							ImageItem imageItem = new ImageItem();
							imageItem.setDoc_path(imgList.get(i).getDocPath());
							mDataList.add(imageItem);
						}
                        taskSno = rBean.getTask_sno();
                        initGridView();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(ReportTaskFinishActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

}
