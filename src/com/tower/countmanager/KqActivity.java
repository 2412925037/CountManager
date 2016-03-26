package com.tower.countmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.tower.countmanager.bean.AttentanceBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.XCRoundImageView;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KqActivity extends Activity {

	private static final String TAG = "KqActivity";

	@ViewInject(R.id.subview_title)
	TextView title;
    @ViewInject(R.id.subview_title_image)
    TextView titleRight;
    @ViewInject(R.id.kq_listview)
    PullToRefreshListView mPullRefreshListView;
    private Context mAppContext;
    ListView listview;
    
    private KqAdapter mAdapter = null;
    List<AttentanceBean> list;
    String[] str = {"正常","异常","休假"};
    private int start = 0;
    //是否加载下一页
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kq);
        mAppContext =  this.getApplicationContext();
        ViewUtils.inject(this);
        initViews();
    }

	private void initViews() {
    	
        title.setText(R.string.kq_title);
        titleRight.setText(R.string.work_screening);
        titleRight.setVisibility(View.VISIBLE);

        listview = mPullRefreshListView.getRefreshableView();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				AttentanceBean bean = list.get(position - 1);
				Intent intent = new Intent(KqActivity.this,KqDetailActivity.class);
                intent.putExtra("name", bean.getEmp_name());
                intent.putExtra("time", bean.getCheck_time());
                intent.putExtra("empId", bean.getEmp_id());
				startActivity(intent);
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

		list = new ArrayList<AttentanceBean>();
        mAdapter = new KqAdapter();
        listview.setAdapter(mAdapter);
        init();
    }
	
	
	
	 private void init(){
			if("2".equals(Utils.getRoleId(KqActivity.this))) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("emp_name", "");
				map.put("check_start_time", "");
				map.put("check_end_time", "");
				map.put("province", "");
				map.put("city", "");
				map.put("district", "");
				map.put("limit", Const.MAX_DATA_LIMIT);
				map.put("start", "0");
				RequestServerUtils util = new RequestServerUtils();
				util.load2Server(map, Const.ATTENDANCE_QUERY_MANAGERT_LIST, handlerLoad, Utils.getToken(this));//县
			}else{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("emp_name", "");
				map.put("check_start_time", "");
				map.put("check_end_time", "");
				map.put("province", "");
				map.put("city", "");
				map.put("district", "");
				map.put("limit", Const.MAX_DATA_LIMIT);
				map.put("start", "0");
				RequestServerUtils util = new RequestServerUtils();
				util.load2Server(map, Const.ATTENDANCE_QUERY_LIST, handlerLoad, Utils.getToken(this));//区
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
							Toast.makeText(KqActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
									.show();
						} else {
							JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
						  if(resultObj.getInt("total") > 0){
								Gson gson = new Gson();
								Type listType = new TypeToken<ArrayList<AttentanceBean>>() {}.getType();
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
					Toast.makeText(KqActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
					break;
				}
				mPullRefreshListView.onRefreshComplete();
			}
		};
	    
    private void getMoreList(){
		if("2".equals(Utils.getRoleId(KqActivity.this))) {
			Map<String, Object> map = new HashMap<String, Object>();
	    	RequestServerUtils util = new RequestServerUtils();
			map.put("emp_name", "");
			map.put("check_start_time", "");
			map.put("check_end_time", "");
			map.put("province", "");
			map.put("city", "");
			map.put("district", "");
			map.put("limit", Const.MAX_DATA_LIMIT);
			map.put("start", start);
			util.load2Server(map, Const.ATTENDANCE_QUERY_MANAGERT_LIST, morehandlerLoad, Utils.getToken(this));//县
		}else{
			Map<String, Object> map = new HashMap<String, Object>();
	    	RequestServerUtils util = new RequestServerUtils();
			map.put("emp_name", "");
			map.put("check_start_time", "");
			map.put("check_end_time", "");
			map.put("province", "");
			map.put("city", "");
			map.put("district", "");
			map.put("limit", Const.MAX_DATA_LIMIT);
			map.put("start", start);
			util.load2Server(map, Const.ATTENDANCE_QUERY_LIST, morehandlerLoad, Utils.getToken(this));//区
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
						Toast.makeText(KqActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
								.show();
					} else {
						JSONObject resultObj = new JSONObject(jsonObject.getString("result"));

						if(resultObj.getInt("total") > 0){
							Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<AttentanceBean>>() {}.getType();
							List<AttentanceBean> lists = gson.fromJson(resultObj.getString("rows"), listType);
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
				Toast.makeText(KqActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};
    
    @OnClick(value = {R.id.subview_title_arrow,R.id.subview_title_image})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.subview_title_image:
            	if(Utils.getRoleId(mAppContext).equals("3")){
            		startActivity(new Intent(this,KqSearch2Activity.class));
            	}else{
            	startActivity(new Intent(this,KqSearchActivity.class));
            	}
                break;
        }
    }
    
    
    class KqAdapter extends BaseAdapter {

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
                convertView = getLayoutInflater().inflate(R.layout.layout_kq_listview_item, null);
				holder.userIcon = (XCRoundImageView)convertView.findViewById(R.id.item_kq_user_icon);
				holder.userNane = (TextView)convertView.findViewById(R.id.item_kq_name);
				holder.userAddress = (TextView)convertView.findViewById(R.id.item_kq_address);
                holder.kqIcon = (View)convertView.findViewById(R.id.item_kq_icon);
				holder.kqType = (TextView)convertView.findViewById(R.id.item_kq_type);
				holder.kqDate = (TextView)convertView.findViewById(R.id.item_kq_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AttentanceBean bean = list.get(position);
            holder.userNane.setText(bean.getEmp_name());
            holder.userAddress.setText(bean.getProvince()+"  "+bean.getCity()+"  "+bean.getDistrict());
            holder.kqDate.setText(bean.getCheck_time());
            holder.kqType.setText(bean.getResult());
            
            if(str[0].equals(holder.kqType.getText().toString())){
            	holder.kqIcon.setBackgroundResource(R.color.kq_item_normal_text);
            	holder.kqType.setTextColor(getResources().getColor(R.color.kq_item_normal_text));
            }else if(str[1].equals(holder.kqType.getText().toString())){
            	holder.kqIcon.setBackgroundResource(R.color.kq_item_error_text);
            	holder.kqType.setTextColor(getResources().getColor(R.color.kq_item_error_text));
            }else if(str[2].equals(holder.kqType.getText().toString())){
            	holder.kqIcon.setBackgroundResource(R.color.kq_item_vacation_text);
            	holder.kqType.setTextColor(getResources().getColor(R.color.kq_item_vacation_text));
            }
            if(!TextUtils.isEmpty(bean.getUser_img())) {

                AsyncImageLoader loader = new AsyncImageLoader(holder.userIcon);
                loader.execute(bean.getUser_img());
            }
            return convertView;
        }

        class ViewHolder {
            XCRoundImageView userIcon;
            TextView userNane;
            TextView userAddress;
            View kqIcon;
            TextView kqType;
            TextView kqDate;
        }
    }
    

	
}
