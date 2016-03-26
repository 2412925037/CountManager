package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.TaskInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 任务详情
 */
public class TaskInfoActivity extends Activity{

	private static final String TAG = "TaskInfoActivity";

	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
    @ViewInject(R.id.task_info_type_view)
    private TextView typeView;
    @ViewInject(R.id.task_info_type_sub_view)
    private TextView typeSubView;
    @ViewInject(R.id.task_info_no_view)
    private TextView taskNoView;
    @ViewInject(R.id.task_info_name_view)
    private TextView taskNameView;
    @ViewInject(R.id.task_info_desc_view)
    private TextView taskDescView;
    @ViewInject(R.id.task_info_person_view)
    private TextView personView;
    @ViewInject(R.id.task_info_site_name_view)
    private TextView siteNameView;
    @ViewInject(R.id.task_info_site_lat_view)
    private TextView siteLatView;
    @ViewInject(R.id.task_info_site_lon_view)
    private TextView siteLonView;
    @ViewInject(R.id.task_info_site_address_view)
    private TextView siteAddressView;
    @ViewInject(R.id.task_info_complete_time_view)
    private TextView completeTimeView;
    @ViewInject(R.id.task_info_emergency_view)
    private TextView emergencyView;
    @ViewInject(R.id.task_info_feedback_view)
    private TextView feedbackView;


	private ProgressDialog dialog = null;
	String workId = "";
    String taskSno = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_info);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		if("confirming".equals(getIntent().getStringExtra("task"))){
			subviewTitle.setText("待签收");
		}else{
		subviewTitle.setText(R.string.task_info_title);
		}
		workId = getIntent().getStringExtra("workId");
        taskSno = getIntent().getStringExtra("task_sno");
        loadProjectInfo();
	}

    @OnClick(value = {R.id.subview_title_arrow, R.id.task_info_voice_layout})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.task_info_voice_layout:
                startActivity(new Intent(this, VoiceDetailActivity.class).putExtra("taskSno", taskSno));
                break;

        }
    }

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}
	
	private void loadProjectInfo() {
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
		util.load2Server(map, Const.GET_TASK_INFO_URL, handlerLoad, Utils.getToken(this));
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
						Toast.makeText(TaskInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else if ("01".equals(success)) {
                        Gson gson = new Gson();
                        TaskInfoBean bean = gson.fromJson(jsonObject.getString("result"), TaskInfoBean.class);

                        typeView.setText(bean.getTask_type_name());
                        typeSubView.setText(bean.getTask_type2());
                        taskNoView.setText(bean.getTask_code());
                        taskNameView.setText(bean.getTask_name());
                        taskDescView.setText(bean.getTask_info());
                        personView.setText(bean.getEmp_names());
                        siteNameView.setText(bean.getSite_name());
                        if (!TextUtils.isEmpty(bean.getLatitude())) {
                        	siteLatView.setText(String.format(getString(R.string.auto_operation_location_latitude), bean.getLatitude()));
						}
                        if (!TextUtils.isEmpty(bean.getLongitude())){
                        	siteLonView.setText(String.format(getString(R
                        			.string.auto_operation_location_longitude), bean.getLongitude()));
                        }
                        siteAddressView.setText(bean.getProvince() + " " + bean.getCity() + " " + bean.getDistrict());
                        completeTimeView.setText(bean.getTime_limit());
                        if(bean.getUrgency().equals("1")){
                        	emergencyView.setText("紧急");
                        }else{
                        	emergencyView.setText("一般");
                        }
                        if(bean.getMulti_feedback().equals("1"))
                            feedbackView.setText(R.string.task_info_multi_more);
                        else
                            feedbackView.setText(R.string.task_info_multi_normal);
                        taskSno = bean.getTask_sno();
					} else
                        Utils.sessionTimeout(TaskInfoActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(TaskInfoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
