package com.tower.countmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.KqActivity.KqAdapter;
import com.tower.countmanager.bean.AttentanceBean;
import com.tower.countmanager.bean.TaskListBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MissionSelectActivity extends Activity {

	private static final String TAG = "MissionSelectActivity";
    private Context mAppContext = null;
    @ViewInject(R.id.subview_title)
    TextView title;
    @ViewInject(R.id.mission_listview)
    PullToRefreshListView mPullRefreshListView;
    ListView listview;

    MissionAdapter mAdapter = null;
    List<TaskListBean> list ;
    private int start = 0;
    //是否加载下一页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);
        mAppContext = this.getApplicationContext();
        ViewUtils.inject(this);
        initView();
        
    }

	private void initView() {
        title.setText(R.string.mission_title);
        
        listview = mPullRefreshListView.getRefreshableView();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				String task_kind = list.get(position-1).getTask_kind();
				TaskListBean bean = list.get(position-1);
				if(bean.getTask_state().equals("待确认")){
					startActivity(new Intent(mAppContext,TaskProcessingEvaluationActvity.class).putExtra("task_sno", bean.getTask_sno()).putExtra("type", "finish").putExtra("task", "confirming"));
				}
				if(bean.getTask_state().equals("待签收")){
					startActivity(new Intent(mAppContext, TaskInfoActivity.class).putExtra("task_sno", bean.getTask_sno()).putExtra("task", "confirming"));
				}
				if(bean.getTask_state().equals("执行中")){
					//startActivity(new Intent(mAppContext, TodoActivity.class).putExtra("task_sno", bean.getTask_sno()));
					startActivity(new Intent(mAppContext,TaskWaitReadActivity.class).putExtra("task_sno", bean.getTask_sno()).putExtra("type", "task2"));
				}
				if(bean.getTask_state().equals("已完成")){
					startActivity(new Intent(mAppContext,TaskWaitReadActivity.class).putExtra("task_sno", bean.getTask_sno()).putExtra("type", "task").putExtra("task", "over"));
				}
				
			}
		});
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				init();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMoreList();
			}

		});
		list =  new ArrayList<TaskListBean>();
        mAdapter = new MissionAdapter();
        listview.setAdapter(mAdapter);
        init();
    }
	
	private void init(){
		Log.e(TAG, ""+getIntent().getStringExtra("province")+getIntent().getStringExtra("city")+getIntent().getStringExtra("county"));
		if("2".equals(Utils.getRoleId(MissionSelectActivity.this))){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("task_name", getIntent().getStringExtra("task_name"));
    		map.put("task_state", getIntent().getStringExtra("task_status"));
    		map.put("score", getIntent().getStringExtra("rating"));
    		map.put("sign_user", getIntent().getStringExtra("sign_user"));
    		map.put("multi_feedback", getIntent().getStringExtra("report"));
    		map.put("province", getIntent().getStringExtra("province").trim());
    		map.put("city", getIntent().getStringExtra("city").trim());
    		map.put("district", getIntent().getStringExtra("county").trim());
    		map.put("start_day",getIntent().getStringExtra("start_day"));
    		map.put("end_day",getIntent().getStringExtra("end_day"));
    		map.put("limit", Const.MAX_DATA_LIMIT);
    		map.put("start", "0");
    		RequestServerUtils util = new RequestServerUtils();
    		util.load2Server(map, Const.GET_TASK_LIST_N, handlerLoad, Utils.getToken(this));
    	}else{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("task_name", getIntent().getStringExtra("task_name"));
    		map.put("task_state", getIntent().getStringExtra("task_status"));
    		map.put("score", getIntent().getStringExtra("rating"));
    		map.put("sign_user", getIntent().getStringExtra("sign_user"));
    		map.put("multi_feedback", getIntent().getStringExtra("report"));
    		map.put("province", getIntent().getStringExtra("province").trim());
    		map.put("city", getIntent().getStringExtra("city").trim());
    		map.put("district", getIntent().getStringExtra("county").trim());
    		map.put("start_day",getIntent().getStringExtra("start_day"));
    		map.put("end_day",getIntent().getStringExtra("end_day"));
    		map.put("limit", Const.MAX_DATA_LIMIT);
    		map.put("start", "0");
    		RequestServerUtils util = new RequestServerUtils();
    		util.load2Server(map, Const.GET_TASK_LIST_T, handlerLoad, Utils.getToken(this));
    	}
	}
	
	private Handler handlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(MissionSelectActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else {
						JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
					  if(resultObj.getInt("total") > 0){
						  Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<TaskListBean>>() {}.getType();
							list = gson.fromJson(resultObj.getString("rows"), listType);
							start = resultObj.getInt("start");
						}else {
                            if(list != null)
                                list.clear();
                        }
						mAdapter.notifyDataSetChanged();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(MissionSelectActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};
	
	private void getMoreList(){
		
    	if("2".equals(Utils.getRoleId(MissionSelectActivity.this))){
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("task_name", getIntent().getStringExtra("task_name"));
    		map.put("task_state", getIntent().getStringExtra("task_status"));
    		map.put("score", getIntent().getStringExtra("rating"));
    		map.put("sign_user", getIntent().getStringExtra("sign_user"));
    		map.put("multi_feedback", getIntent().getStringExtra("report"));
    		map.put("province", getIntent().getStringExtra("province").trim());
    		map.put("city", getIntent().getStringExtra("city").trim());
    		map.put("district", getIntent().getStringExtra("county").trim());
    		map.put("start_day",getIntent().getStringExtra("start_day"));
    		map.put("end_day",getIntent().getStringExtra("end_day"));
    		map.put("limit", Const.MAX_DATA_LIMIT);
    		map.put("start", start);
    		RequestServerUtils util = new RequestServerUtils();
    		util.load2Server(map, Const.GET_TASK_LIST_N, morehandlerLoad, Utils.getToken(this));
    	}else{
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("task_name", getIntent().getStringExtra("task_name"));
    		map.put("task_state", getIntent().getStringExtra("task_status"));
    		map.put("score", getIntent().getStringExtra("rating"));
    		map.put("sign_user", getIntent().getStringExtra("sign_user"));
    		map.put("multi_feedback", getIntent().getStringExtra("report"));
    		map.put("province", getIntent().getStringExtra("province").trim());
    		map.put("city", getIntent().getStringExtra("city").trim());
    		map.put("district", getIntent().getStringExtra("county").trim());
    		map.put("start_day",getIntent().getStringExtra("start_day"));
    		map.put("end_day",getIntent().getStringExtra("end_day"));
    		map.put("limit", Const.MAX_DATA_LIMIT);
    		map.put("start", start);
    		RequestServerUtils util = new RequestServerUtils();
    		util.load2Server(map, Const.GET_TASK_LIST_T, morehandlerLoad, Utils.getToken(this));
    	}
	}
	
	private Handler morehandlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(MissionSelectActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else {
						JSONObject resultObj = new JSONObject(jsonObject.getString("result"));

						if(resultObj.getInt("total") > 0){
							Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<TaskListBean>>() {}.getType();
							List<TaskListBean> lists = gson.fromJson(resultObj.getString("rows"), listType);
							list.addAll(lists);
							start = resultObj.getInt("start");
							mAdapter.notifyDataSetChanged();
							}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(MissionSelectActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};

    @OnClick(value = {R.id.subview_title_arrow, R.id.subview_title_image})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.subview_title_image:
                startActivity(new Intent(this, MissionSearchActivity.class));
                break;
        }
    }
    

    class MissionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.layout_mission_listview_item, null);
                holder.icon = (ImageView) convertView.findViewById(R.id.item_mission_icon);
                holder.name = (TextView) convertView.findViewById(R.id.item_mission_name);
                holder.content = (TextView) convertView.findViewById(R.id.item_mission_content);
                holder.state = (TextView) convertView.findViewById(R.id.item_mission_state);
                holder.userNane = (TextView) convertView.findViewById(R.id.item_mission_user_name);
                holder.time = (TextView) convertView.findViewById(R.id.item_mission_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            TaskListBean bean = list.get(position);
            holder.name.setText(bean.getTask_name());
            holder.content.setText(bean.getTask_type_name());
            holder.state.setText(bean.getTask_state());
            holder.userNane.setText(bean.getOperator());
            holder.time.setText(bean.getOperate_time());
            String task_kind = list.get(position).getTask_kind();
            if(task_kind.equals("N")){
            	holder.icon.setImageResource(R.drawable.n_icon);
			}else{
				holder.icon.setImageResource(R.drawable.t_icon);
			}
            
            return convertView;
        }

        class ViewHolder {
            ImageView icon;
            TextView name;
            TextView content;
            TextView state;
            TextView userNane;
            TextView time;
        }
    }

}
