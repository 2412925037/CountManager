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

public class TodoActivity extends Activity {

	private static final String TAG = "TodoActivity";

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

	private ProgressDialog dialog = null;
	private List<FeedBackInfoBean> mData = new ArrayList<FeedBackInfoBean>();
	private TodoProgressAdapter mAdapter = null;
	String workId = "";
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
		title.setText(R.string.todo_title);
		workId = getIntent().getStringExtra("workId");
		taskSno = getIntent().getStringExtra("task_sno");
		if (!TextUtils.isEmpty(workId)) {
			loadProjectInfo(workId);
		}
	}

	private void init() {
		mAdapter = new TodoProgressAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				if (position == 0) {
                    Intent i = new Intent(TodoActivity.this, TodoCreateBackActivity.class);
                    i.putExtra("taskSno", taskSno);
                    startActivityForResult(i, 90);
                }else {
    				FeedBackInfoBean fBean = (FeedBackInfoBean) adapterView.getItemAtPosition(position);
    				if (!TextUtils.isEmpty(fBean.getScore())) {
    					startActivity(new Intent(TodoActivity.this, TaskProcessingEvaluationActvity.class).putExtra("workId",
    							workId).putExtra("type", "finish"));
    				}else if (!TextUtils.isEmpty(fBean.getFeedback_sno())) {
    					startActivity(new Intent(TodoActivity.this, TodoBackInfoDetailActivity.class).putExtra("feedBackSno",
    							fBean.getFeedback_sno()).putExtra("workId",workId).putExtra("taskSno", taskSno));
    				}
    				
    			
				}
			}
		});
	}

	@OnClick(value = {R.id.subview_title_arrow, R.id.todo_commit_button, R.id.todo_task_view })
	public void onButtonClick(View v) {

		switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.todo_commit_button:
                if (!Utils.isConnect(this)) {
                    Toast.makeText(this, R.string.app_connecting_network_no_connect, Toast.LENGTH_SHORT).show();
                } else {
                    commitButton();
                }
                break;
            case R.id.todo_task_view:
                startActivity(new Intent(this,TaskInfoActivity.class).putExtra("workId", workId));
                break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 90 && resultCode == 90) {

            if(mData != null)
                mData.clear();
            loadProjectInfo(workId);
        }
    }

    class TodoProgressAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size() + 1;
		}

		@Override
		public FeedBackInfoBean getItem(int i) {
			return i == 0 ? new FeedBackInfoBean() : mData.get(i - 1);
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

			if (position == 0) {
				viewHolder.pIcon.setImageResource(R.drawable.item_todo_create_task);
                viewHolder.createView.setText(R.string.todo_progress_create);
                viewHolder.createView.setVisibility(View.VISIBLE);
                viewHolder.taskLayout.setVisibility(View.GONE);
			}else {
                viewHolder.createView.setVisibility(View.GONE);
                viewHolder.taskLayout.setVisibility(View.VISIBLE);
				FeedBackInfoBean fBean = getItem(position);
				if (position == getCount() - 1) {
					viewHolder.pIcon.setImageResource(R.drawable.item_todo_step_task_end);
				} else {
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
			}
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
		handler.removeCallbacksAndMessages(null);
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}

	private void loadProjectInfo(String workItemId) {
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
						Toast.makeText(TodoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							taskTitle.setText(jsonObject2.getString("task_name"));
							taskName.setText(jsonObject2.getString("operator"));
							taskNumber.setText(jsonObject2.getString("task_code"));
							taskContent.setText(jsonObject2.getString("task_type_name"));
							taskTime.setText(jsonObject2.getString("operate_time"));
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
				Toast.makeText(TodoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

    private void commitButton() {
    	boolean isFeedBackListNull = false;
    	//校验无新增反馈时不可上传
    	for (FeedBackInfoBean backInfoBean : mData) {
			if (!TextUtils.isEmpty(backInfoBean.getFeedback_sno())) {
				isFeedBackListNull = true;
				break;
			}
		}
		if (!isFeedBackListNull) {
			Toast.makeText(this, R.string.auto_operation_tips_no_feedback, Toast.LENGTH_SHORT).show();
			return;
		}
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workItemId", workId);
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.WORKER_TODO_COMMIT_URL, handler, Utils.getToken(this));
    }
    

    private Handler handler = new Handler() {
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
                            Toast.makeText(TodoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){
                            Toast.makeText(TodoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else
                            Utils.sessionTimeout(TodoActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
