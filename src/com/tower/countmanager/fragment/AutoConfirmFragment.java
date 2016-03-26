package com.tower.countmanager.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.tower.countmanager.DraftDetail2Activity;
import com.tower.countmanager.DraftDetailActivity;
import com.tower.countmanager.DraftReportTaskDetail2Activity;
import com.tower.countmanager.DraftReportTaskDetailActivity;
import com.tower.countmanager.R;
import com.tower.countmanager.ReportTaskActivity;
import com.tower.countmanager.ReportTaskFinishActivity;
import com.tower.countmanager.TaskInfoActivity;
import com.tower.countmanager.TaskProcessingEvaluationActvity;
import com.tower.countmanager.TodoActivity;
import com.tower.countmanager.bean.DraftBoxBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
/**
 * 草稿箱-->待确认
 * @author WUSONG
 *
 */
public class AutoConfirmFragment extends Fragment {
	private static final String TAG = "AutoConfirmFragment";
	@ViewInject(R.id.auto_project_list)
	PullToRefreshListView mPullRefreshListView;
    
	ListView listView;
	DraftBoxAdapter pa = null;
	List<DraftBoxBean> list=null;
	private int start = 0;
    private Context mAppContext = null;
    private ProgressDialog dialog = null;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAppContext = inflater.getContext().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_auto_project, null);
		ViewUtils.inject(this, view);
		initView(); 

		return view;
	}
	private void initView() {
		
		listView = mPullRefreshListView.getRefreshableView();
		listView.setOnItemClickListener(new OnItemClickListener() {
 
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				String task_sno = list.get(arg2 - 1).getTask_sno();
				String task_kind =  list.get(arg2-1).getTask_kind();
				 if (Utils.getRoleId(mAppContext).equals("2")) {
					 
					startActivity(new Intent(mAppContext,TaskProcessingEvaluationActvity.class).putExtra("task_sno", task_sno).putExtra("type", "finish").putExtra("task", "confirming"));
					 
				 }else{
					 startActivity(new Intent(mAppContext,TaskInfoActivity.class).putExtra("task_sno", task_sno).putExtra("task", "confirming"));
				 }
				
			}
		});
		mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				init(true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				getMoreList(true);
			}
		});
		list=new ArrayList<DraftBoxBean>();
		pa = new DraftBoxAdapter();
		listView.setAdapter(pa);
		init(true);
	}
	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(getActivity(), null,
					getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}
	   private void init(boolean ifDialog) {
		   if (ifDialog) {
				createDialog();
			}
	        Map<String, Object> map = new HashMap<String, Object>();
	        if (Utils.getRoleId(mAppContext).equals("2")) {
		        map.put("task_state", "3");
		        map.put("task_name", "");
		        map.put("start_day", "");
		        map.put("end_day", "");
		        map.put("start", "0");
		        map.put("limit", Const.MAX_DATA_LIMIT);
		        RequestServerUtils util = new RequestServerUtils();
		        util.load2Server(map, Const.DRAFTBOX_LIST_URL, handler, Utils.getToken(mAppContext));	
	        }else{
		        map.put("task_state", "2");
		        map.put("task_name", "");
		        map.put("start_day", "");
		        map.put("end_day", "");
		        map.put("start", "0");
		        map.put("limit", Const.MAX_DATA_LIMIT);
		        RequestServerUtils util = new RequestServerUtils();
		        util.load2Server(map, Const.DRAFTBOX_LIST_URL, handler, Utils.getToken(mAppContext));	
	        }

	    }

	    private Handler handler = new Handler() {
	        public void handleMessage(android.os.Message msg) {
	        	if (dialog != null)
					dialog.dismiss();
	            Log.e(TAG, (String) msg.obj);
	            switch (msg.what) {
	                case Const.REQUEST_SERVER_SUCCESS:
	                    try {
	                        JSONObject jsonObject = new JSONObject((String)msg.obj);
	                        String success = jsonObject.getString("success");
	                        if("00".equals(success)) {
	                            Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
	                        } else {
	                            JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
	                            if(resultObj.getInt("total") > 0) {

	                                Gson gson = new Gson();
	                                Type listType = new TypeToken<ArrayList<DraftBoxBean>>(){}.getType();
	                                list = gson.fromJson(resultObj.getString("rows"), listType);
	                                start = resultObj.getInt("start");
	                            } else {
	                                if(list != null)
	                                    list.clear();
	                            }
	                            pa.notifyDataSetChanged();
	                        }
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }

	                    break;
	                case Const.REQUEST_SERVER_FAILURE:
	                    //登录失败
	                    Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
	                    break;
	            }
	            mPullRefreshListView.onRefreshComplete();
	        }
	    };

	    private void getMoreList(boolean ifDialog) {
	    	   if (ifDialog) {
					createDialog();
				}
	        Map<String, Object> map = new HashMap<String, Object>();
	        if (Utils.getRoleId(mAppContext).equals("2")) {
	            map.put("task_state", "3");
		        map.put("task_name", "");
		        map.put("start_day", "");
		        map.put("end_day", "");
		        map.put("start", start);
		        map.put("limit", Const.MAX_DATA_LIMIT);
		        RequestServerUtils util = new RequestServerUtils();
		        util.load2Server(map, Const.DRAFTBOX_LIST_URL, moreHandler, Utils.getToken(mAppContext));
	        }else{
		        map.put("task_state", "2");
		        map.put("task_name", "");
		        map.put("start_day", "");
		        map.put("end_day", "");
		        map.put("start", start);
		        map.put("limit", Const.MAX_DATA_LIMIT);
		        RequestServerUtils util = new RequestServerUtils();
		        util.load2Server(map, Const.DRAFTBOX_LIST_URL, moreHandler, Utils.getToken(mAppContext));
	        }

	    }

	    private Handler moreHandler = new Handler() {
	        public void handleMessage(android.os.Message msg) {
	        	if (dialog != null)
					dialog.dismiss();
	            Log.e(TAG, (String) msg.obj);
	            switch (msg.what) {
	                case Const.REQUEST_SERVER_SUCCESS:
	                    try {
	                        JSONObject jsonObject = new JSONObject((String)msg.obj);
	                        String success = jsonObject.getString("success");
	                        if("00".equals(success)) {
	                            Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
	                        } else {
	                            JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
	                            if(resultObj.getInt("total") > 0) {

	                                Gson gson = new Gson();
	                                Type listType = new TypeToken<ArrayList<DraftBoxBean>>(){}.getType();
	                                ArrayList<DraftBoxBean> list = gson.fromJson(resultObj.getString("rows"), listType);
	                                list.addAll(list);
	                                start = resultObj.getInt("start");
	                                pa.notifyDataSetChanged();
	                            }
	                        }
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }

	                    break;
	                case Const.REQUEST_SERVER_FAILURE:
	                    //登录失败
	                    Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
	                    break;
	            }
	            mPullRefreshListView.onRefreshComplete();
	        }
	    };

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Activity.RESULT_FIRST_USER:
				if (data != null) {
					boolean isRefresh = data.getBooleanExtra("isRefresh", false);
					if (isRefresh) {
						// 手动刷新待办项目
						//refresh();
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * 草稿箱Adapter
	 */
	class DraftBoxAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			View view = null;

			if (convertView == null) {
                convertView = LayoutInflater.from(mAppContext).inflate(R.layout.layout_item_list_draft_box, null);
				holder = new ViewHolder();
				holder.work_project_name=(TextView)convertView.findViewById(R.id.work_project_name);
				holder.tv_style=(TextView)convertView.findViewById(R.id.tv_style);
				holder.tv_waitsure=(TextView)convertView.findViewById(R.id.tv_waitsure);
				holder.work_people_name=(TextView)convertView.findViewById(R.id.work_people_name);
				holder.work_auto_sign_time=(TextView)convertView.findViewById(R.id.work_auto_sign_time);
				holder.layout_operation=(RelativeLayout)convertView.findViewById(R.id.layout_operation);
				convertView.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			DraftBoxBean bean = list.get(position);
			
			holder.work_project_name.setText(bean.getTask_name());
			holder.tv_style.setText(bean.getTask_type_name()+"--"+bean.getTask_type2());
			if (Utils.getRoleId(mAppContext).equals("2")) {
				holder.tv_waitsure.setText(R.string.draft_box_auto_confirm);
			}else{
				holder.tv_waitsure.setText(R.string.work_auto_sign);
			}
			
			holder.work_people_name.setText(bean.getOperator());
			holder.work_auto_sign_time.setText(bean.getOperate_time());
			holder.layout_operation.setVisibility(View.GONE);
			return convertView;
		}

		class ViewHolder {
			TextView work_project_name;
			TextView tv_style;
			TextView tv_waitsure;
			TextView work_people_name;
			TextView work_auto_sign_time;
			RelativeLayout layout_operation;
		}

	}
}
