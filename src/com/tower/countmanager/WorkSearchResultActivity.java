package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.tower.countmanager.bean.WorkBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkSearchResultActivity extends Activity {

	private static final String TAG = "WorkSearchResultActivity";

	@ViewInject(R.id.subview_title)
	private TextView title;
    @ViewInject(R.id.work_result_listview)
    private PullToRefreshListView mPullRefreshListView;

    ListView listView;
    List<WorkBean> worklist;
    WorkListAdapter adapter;
    private int start = 0;
    private String workId = "";

    ProgressDialog dialog = null;
    String titleValue = "";
    String typeId = "";
    String startDate = "";
    String endDate = "";
    int tag = -1;
    String workItemState = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_search_result);
		ViewUtils.inject(this);

        init();
	}

    private void init() {
        title.setText(R.string.mission_title);

        listView = mPullRefreshListView.getRefreshableView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                workId = worklist.get(position - 1).getWorkItemId();
                if (Utils.getRoleId(WorkSearchResultActivity.this).equals("2")) {
                    startActivity(new Intent(WorkSearchResultActivity.this, TodoActivity.class).putExtra("workId", workId));
                } else {
                    // 区域经理
                    startActivity(new Intent(WorkSearchResultActivity.this, TaskProcessingEvaluationActvity.class).putExtra("workId",
                            workId).putExtra("type", "auto"));
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

        worklist = new ArrayList<WorkBean>();
        adapter = new WorkListAdapter();
        listView.setAdapter(adapter);

        Intent i = getIntent();
        titleValue = i.getStringExtra("title");
        typeId = i.getStringExtra("typeId");
        startDate = i.getStringExtra("startDate");
        endDate = i.getStringExtra("endDate");
        tag = i.getIntExtra("tag", -1);
        if(tag == 0) {
            workItemState = "1";
        } else if(tag == 1) {
            workItemState = "2";
        } else if(tag == 2) {
            workItemState = "4";
        }
        initData();
    }

	@OnClick(value = {R.id.subview_title_arrow})
	public void onButtonClick(View v){

		switch(v.getId()) {
            case R.id.subview_title_arrow :
                finish();
                break;
		}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void createDialog() {
        if(dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

    private void initData() {
        createDialog();
        Map<String, Object> map = new HashMap<String, Object>();

        if (Utils.getRoleId(this).equals("2")) {
            map.put("workItemState", workItemState);
            map.put("title", titleValue);
            map.put("typeId", typeId);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("start", "0");
            map.put("limit", Const.MAX_DATA_LIMIT);
            RequestServerUtils util = new RequestServerUtils();
            util.load2Server(map, Const.PROJECT_LIST_URL, handler, Utils.getToken(this));
        }else{
            if(tag == 0) {
                map.put("title", titleValue);
                map.put("typeId", typeId);
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                map.put("start", "0");
                map.put("limit", Const.MAX_DATA_LIMIT);
                RequestServerUtils util = new RequestServerUtils();
                util.load2Server(map, Const.WAIT_READ_URL, handler, Utils.getToken(this));
            } else {
                map.put("workItemState", workItemState);
                map.put("title", titleValue);
                map.put("typeId", typeId);
                map.put("startDate", startDate);
                map.put("endDate", endDate);
                map.put("start", "0");
                map.put("limit", Const.MAX_DATA_LIMIT);
                RequestServerUtils util = new RequestServerUtils();
                util.load2Server(map, Const.PROJECT_LIST_URL, handler, Utils.getToken(this));
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if(dialog != null)
                dialog.dismiss();
            Log.e(TAG, "new data: " + (String) msg.obj);
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject((String)msg.obj);
                        String success = jsonObject.getString("success");
                        if("00".equals(success)) {
                            Toast.makeText(WorkSearchResultActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
                            if(resultObj.getInt("total") > 0) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<WorkBean>>(){}.getType();
                                worklist = gson.fromJson(resultObj.getString("rows"), listType);
                                start = resultObj.getInt("start");
                            } else {
                                if(worklist != null)
                                    worklist.clear();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    //登录失败
                    Toast.makeText(WorkSearchResultActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
            mPullRefreshListView.onRefreshComplete();
        }
    };

    private void getMoreList() {
        Map<String, Object> map = new HashMap<String, Object>();
        if (Utils.getRoleId(this).equals("2")) {
            map.put("workItemState", workItemState);
            map.put("title", titleValue);
            map.put("typeId", typeId);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("start", start);
            map.put("limit", Const.MAX_DATA_LIMIT);
            RequestServerUtils util = new RequestServerUtils();
            util.load2Server(map, Const.PROJECT_LIST_URL, moreHandler, Utils.getToken(this));
        }else {
            map.put("title", titleValue);
            map.put("typeId", typeId);
            map.put("startDate", startDate);
            map.put("endDate", endDate);
            map.put("start", start);
            map.put("limit", Const.MAX_DATA_LIMIT);
            RequestServerUtils util = new RequestServerUtils();
            util.load2Server(map, Const.WAIT_READ_URL, moreHandler, Utils.getToken(this));
        }
    }

    private Handler moreHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            Log.e(TAG, "more data: " + (String) msg.obj);
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject((String)msg.obj);
                        String success = jsonObject.getString("success");
                        if("00".equals(success)) {
                            Toast.makeText(WorkSearchResultActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
                            if(resultObj.getInt("total") > 0) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<WorkBean>>(){}.getType();
                                ArrayList<WorkBean> list = gson.fromJson(resultObj.getString("rows"), listType);
                                worklist.addAll(list);
                                start = resultObj.getInt("start");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    //登录失败
                    Toast.makeText(WorkSearchResultActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
            mPullRefreshListView.onRefreshComplete();
        }
    };

    class WorkListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return worklist.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup arg2) {

            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.layout_item_list_auto_project, null);
                holder.btnSign = (TextView) convertView.findViewById(R.id.btn_sign);
                holder.icon = (ImageView) convertView.findViewById(R.id.img_auto_sign);
                holder.type = (TextView) convertView.findViewById(R.id.work_project_type);
                holder.title = (TextView) convertView.findViewById(R.id.work_project_title);
                holder.content = (TextView)convertView.findViewById(R.id.work_project_typeName);
                holder.person = (TextView)convertView.findViewById(R.id.work_project_empName);
                holder.time = (TextView)convertView.findViewById(R.id.work_auto_createTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.icon.setImageResource(R.drawable.icon_auto);
            if(workItemState.equals("1")) {
                if(Utils.getRoleId(WorkSearchResultActivity.this).equals("1"))
                    holder.type.setText(R.string.work_auto_read);
                else
                    holder.type.setText(R.string.work_auto_sign);
            } else if(workItemState.equals("2"))
                holder.type.setText(R.string.work_auto);
            else if(workItemState.equals("4"))
                holder.type.setText(R.string.work_finish);
            WorkBean bean = worklist.get(position);
            //签收按钮隐藏
            holder.btnSign.setVisibility(View.GONE);
            holder.title.setText(bean.getTitle());
            holder.content.setText(bean.getTypeName());
            holder.person.setText(bean.getEmpName());
            holder.time.setText(bean.getCreateTime());
            return convertView;
        }

    }

    class ViewHolder {
        public ImageView icon;
        public TextView btnSign;
        public TextView title;
        public TextView content;
        public TextView person;
        public TextView time;
        public TextView type;
    }
}
	
