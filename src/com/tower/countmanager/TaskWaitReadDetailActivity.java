package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.FeedBackInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待阅详情
 * @author WUSONG
 *
 */
public class TaskWaitReadDetailActivity extends Activity {

	private static final String TAG = "TaskWaitReadActivity";

	@ViewInject(R.id.subview_title)
	private TextView title;
	@ViewInject(R.id.todo_listview)
	private ListView mListView;

	@ViewInject(R.id.todo_task_title_view)
	private TextView taskTitle;
	@ViewInject(R.id.todo_task_name_view)
	private TextView taskName;
	@ViewInject(R.id.todo_task_number_view)
	private TextView taskNumber;
	@ViewInject(R.id.todo_task_content_view)
	private TextView taskContent;
	@ViewInject(R.id.todo_task_time_view)
	private TextView taskTime;
	@ViewInject(R.id.todo_commit_button)
	private Button todo_commit_button;
	private ProgressDialog dialog = null;
	private List<FeedBackInfoBean> mData = new ArrayList<FeedBackInfoBean>();
	private TodoProgressAdapter mAdapter = null;
	String task_sno = "";
	String workItemId = "";
	String taskKind = "";
	String taskSno = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);
		ViewUtils.inject(this);
		initViews();
	}

	private void initViews() {
		String type = getIntent().getStringExtra("type");
		if ("finish".equals(type)) {
			workItemId = getIntent().getStringExtra("workItemId");
			task_sno = getIntent().getStringExtra("task_sno");
			title.setText(R.string.work_info);
			if (!TextUtils.isEmpty(workItemId)) {
				loadFinishProjectInfo(workItemId);
			}else if (!TextUtils.isEmpty(task_sno)) {
				loadProjectInfo(task_sno);
			}
		}else if("task".equals(type)){
				title.setText(R.string.mission_task_over);
				task_sno = getIntent().getStringExtra("task_sno");
				if (!TextUtils.isEmpty(task_sno)) {
					loadProjectInfo(task_sno);
				}
		}else if("task2".equals(type)){
			title.setText(R.string.mission_task_readying);
			task_sno = getIntent().getStringExtra("task_sno");
			if (!TextUtils.isEmpty(task_sno)) {
				loadProjectInfo(task_sno);
			}
		}else{
			title.setText(R.string.work_auto_read);
			task_sno = getIntent().getStringExtra("task_sno");
			if (!TextUtils.isEmpty(task_sno)) {
				loadProjectInfo(task_sno);
			}
			
		}
		todo_commit_button.setVisibility(View.GONE);
	}

	private void init() {
		mAdapter = new TodoProgressAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				FeedBackInfoBean fBean = (FeedBackInfoBean) adapterView.getItemAtPosition(position);
				if (!TextUtils.isEmpty(fBean.getScore())) {
					Intent intent = new Intent(TaskWaitReadDetailActivity.this, TaskProcessingEvaluationActvity.class);
					intent.putExtra("type", "finish");
					intent.putExtra("task_sno", taskSno);
					intent.putExtra("type", "finish");
					intent.putExtra("taskInfoNotClick", true);
					startActivity(intent);
				}else if (!TextUtils.isEmpty(fBean.getFeedback_sno())) {
					startActivity(new Intent(TaskWaitReadDetailActivity.this, TodoBackInfoDetailActivity.class).putExtra("feedBackSno",
							fBean.getFeedback_sno()).putExtra("workId",workItemId).putExtra("task_sno", taskSno));
				}
				
			}
		});
	}

	@OnClick(value = {R.id.subview_title_arrow,R.id.todo_task_view})
	public void onButtonClick(View v) {
		switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.todo_task_view:
				if ("T".equals(taskKind)) {
					startActivity(new Intent(this, TaskInfoActivity.class).putExtra("task_sno", taskSno));
				} else {
					startActivity(new Intent(this, ReportTaskFinishActivity.class).putExtra("workId", workItemId).putExtra(
                            "task_sno", taskSno));
				}
				break;
		}
	}

	class TodoProgressAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size() ;
		}

		@Override
		public FeedBackInfoBean getItem(int i) {
			return mData.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewGroup) {

			ViewHolder viewHolder = null;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.layout_todo_listview_item, null);
				viewHolder.pIcon = (ImageView) convertView.findViewById(R.id.item_todo_img);
                viewHolder.createView = (TextView) convertView.findViewById(R.id.item_todo_create_view);
                viewHolder.taskLayout = (LinearLayout) convertView.findViewById(R.id.item_todo_task_layout);
				viewHolder.pTitle = (TextView) convertView.findViewById(R.id.item_todo_title);
				viewHolder.pContent = (TextView) convertView.findViewById(R.id.item_todo_content);
                viewHolder.pTime = (TextView) convertView.findViewById(R.id.item_todo_time);
				viewHolder.pUser = (TextView) convertView.findViewById(R.id.item_todo_user);
				viewHolder.pRatingBar = (RatingBar) convertView.findViewById(R.id.item_todo_ratingbar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

//			if (position == 0) {
//				viewHolder.pIcon.setImageResource(R.drawable.item_todo_create_task);
//                viewHolder.createView.setText(R.string.todo_progress_create);
//                viewHolder.createView.setVisibility(View.VISIBLE);
//                viewHolder.taskLayout.setVisibility(View.GONE);
//			} else {
				FeedBackInfoBean fBean = mData.get(position);
				if (position == 0) {
					viewHolder.pIcon.setImageResource(R.drawable.item_todo_step_task_start);
				}else if (position == getCount()-1) {
					viewHolder.pIcon.setImageResource(R.drawable.item_todo_step_task_end);
				}else {
					viewHolder.pIcon.setImageResource(R.drawable.item_todo_step_task);
				}
				viewHolder.pTitle.setText(fBean.getStep_name());
                String content = fBean.getComplete_desc();
                if(!TextUtils.isEmpty(content))
				    viewHolder.pContent.setText(content);
                else
                    viewHolder.pContent.setVisibility(View.GONE);
                viewHolder.pTime.setText(String.format(getString(R.string.todo_step_time), fBean.getFeedback_date()));
				viewHolder.pUser.setText(String.format(getString(R.string.todo_step_people), fBean.getOperator()));
				if (!TextUtils.isEmpty(fBean.getScore())) {
					int i = Integer.parseInt(fBean.getScore());
					viewHolder.pRatingBar.setProgress(i);
				}else {
					viewHolder.pRatingBar.setVisibility(View.GONE);
				}
		//	}
			return convertView;
		}

		class ViewHolder {
			public ImageView pIcon;
            public TextView createView;
            public LinearLayout taskLayout;
			public TextView pTitle;
			public TextView pContent;
            public TextView pTime;
			public TextView pUser;
			public RatingBar pRatingBar;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//handler.removeCallbacksAndMessages(null);
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}
	
	private void loadFinishProjectInfo(String workItemId) {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workItemId", workItemId);
		map.put("task_sno", "");
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.PROJECT_INFO_URL, handlerLoad, Utils.getToken(this));
	}

	private void loadProjectInfo(String task_sno) {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workItemId", "");
		map.put("task_sno", task_sno);
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.PROJECT_INFO_URL, handlerLoad, Utils.getToken(this));
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
						Toast.makeText(TaskWaitReadDetailActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							taskTitle.setText(jsonObject2.getString("task_name"));
							taskName.setText(jsonObject2.getString("operator"));
							taskNumber.setText(jsonObject2.getString("task_code"));
							taskContent.setText(jsonObject2.getString("task_type_name"));
							taskTime.setText(jsonObject2.getString("operate_time"));
							taskKind = jsonObject2.getString("task_kind");
                            taskSno = jsonObject2.getString("task_sno");
							Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<FeedBackInfoBean>>() {}.getType();
							String str = jsonObject2.getString("feedBackInfoList");
							mData = gson.fromJson(str, listType);
							init();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(TaskWaitReadDetailActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
