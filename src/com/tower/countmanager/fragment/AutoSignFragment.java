package com.tower.countmanager.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.tower.countmanager.R;
import com.tower.countmanager.TaskInfoActivity;
import com.tower.countmanager.TaskWaitReadActivity;
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

/**
 * tab 县域经理 -待签收   区域经理-待阅
 */
public class AutoSignFragment extends Fragment {

	private static final String TAG = "AutoSignFragment";

	@ViewInject(R.id.auto_project_list)
	PullToRefreshListView mPullRefreshListView;
	@ViewInject(R.id.btn_sign)
	TextView btnSign;

	private Context mAppContext = null;
	private ProgressDialog dialog = null;
	ListView listView;
	List<WorkBean> worklist;
	WorkListAdapter adapter;
	private int start = 0;
	private String tasksno = "";
	private String workId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_auto_project, null);
		ViewUtils.inject(this, view);
		mAppContext = inflater.getContext().getApplicationContext();
		initViews();
		return view;
	}

	private void initViews() {
		listView = mPullRefreshListView.getRefreshableView();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				workId = worklist.get(position - 1).getWorkItemId();
				tasksno = worklist.get(position - 1).getTaskSno();
				 if (Utils.getRoleId(mAppContext).equals("2")) {
					 
					 startActivity(new Intent(mAppContext,TaskInfoActivity.class).putExtra("workId", workId));
				 }else{
					 startActivity(new Intent(mAppContext,TaskWaitReadActivity.class).putExtra("task_sno", tasksno));
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
		init();
	}

	private void createDialog() {
		if (dialog == null) {
			dialog = ProgressDialog.show(getActivity(), null, getString(R.string.app_connecting_dialog_text));
			dialog.setCancelable(false);
		} else
			dialog.show();
	}

	// private void getActivityFromServer() {
	// createDialog();
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put("workItemId", workId);
	// RequestServerUtils util = new RequestServerUtils();
	// util.load2Server(map, Const.PROJECT_URL, openHandler,
	// Utils.getToken(mAppContext));
	// }

	// private Handler openHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	//
	// if(dialog != null)
	// dialog.dismiss();
	// Log.e(TAG, (String) msg.obj);
	// switch (msg.what) {
	// case Const.REQUEST_SERVER_SUCCESS:
	// try {
	// JSONObject jsonObject = new JSONObject((String)msg.obj);
	// String success = jsonObject.getString("success");
	// if("00".equals(success)) {
	// Toast.makeText(mAppContext, jsonObject.getString("resultdesc"),
	// Toast.LENGTH_SHORT).show();
	// } else {
	// JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
	// String business = resultObj.getString("businessUrl");
	// if("KG".equals(business))
	// startActivity(new Intent(mAppContext,
	// CurrentWorkActivity.class).putExtra("workItemId", workId));
	// else if("CL".equals(business))
	// startActivity(new Intent(mAppContext,
	// LookConstructionActivity.class).putExtra("workItemId", workId));
	// else if("SD".equals(business))
	// startActivity(new Intent(mAppContext, ElectricResultActivity.class));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// break;
	// case Const.REQUEST_SERVER_FAILURE:
	// //登录失败
	// Toast.makeText(mAppContext, R.string.app_connnect_failure,
	// Toast.LENGTH_SHORT).show();
	// break;
	// }
	// }
	// };

	private void init() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (Utils.getRoleId(mAppContext).equals("2")) {
			map.put("workItemState", "1");
            map.put("title", "");
            map.put("typeId", "");
            map.put("startDate", "");
            map.put("endDate", "");
			map.put("start", "0");
			map.put("limit", Const.MAX_DATA_LIMIT);
			RequestServerUtils util = new RequestServerUtils();
			util.load2Server(map, Const.PROJECT_LIST_URL, handler, Utils.getToken(mAppContext));
		}else{
            map.put("title", "");
            map.put("typeId", "");
            map.put("startDate", "");
            map.put("endDate", "");
			map.put("start", "0");
			map.put("limit", Const.MAX_DATA_LIMIT);
			RequestServerUtils util = new RequestServerUtils();
			util.load2Server(map, Const.WAIT_READ_URL, handler, Utils.getToken(mAppContext));
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
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					} else {
						JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
						if (resultObj.getInt("total") > 0) {

							Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<WorkBean>>() {
							}.getType();
							worklist = gson.fromJson(resultObj.getString("rows"), listType);
							start = resultObj.getInt("start");
						} else {
							if (worklist != null)
								worklist.clear();
						}
						adapter.notifyDataSetChanged();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case Const.REQUEST_SERVER_FAILURE:
				// 登录失败
				Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};

	private void getMoreList() {
		Map<String, Object> map = new HashMap<String, Object>();
		if (Utils.getRoleId(mAppContext).equals("2")) {
			map.put("workItemState", "1");
            map.put("title", "");
            map.put("typeId", "");
            map.put("startDate", "");
            map.put("endDate", "");
			map.put("start", start);
			map.put("limit", Const.MAX_DATA_LIMIT);
			RequestServerUtils util = new RequestServerUtils();
			util.load2Server(map, Const.PROJECT_LIST_URL, moreHandler, Utils.getToken(mAppContext));
		}else{
            map.put("title", "");
            map.put("typeId", "");
            map.put("startDate", "");
            map.put("endDate", "");
			map.put("start", start);
			map.put("limit", Const.MAX_DATA_LIMIT);
			RequestServerUtils util = new RequestServerUtils();
			util.load2Server(map, Const.WAIT_READ_URL, moreHandler, Utils.getToken(mAppContext));
		}
	
	}

	private Handler moreHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			Log.e(TAG, "more data: " + (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					} else {
						JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
						if (resultObj.getInt("total") > 0) {

							Gson gson = new Gson();
							Type listType = new TypeToken<ArrayList<WorkBean>>() {
							}.getType();
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
				// 登录失败
				Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};

	private Handler handlerSign = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					if ("01".equals(success)) {
						refresh();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case Const.REQUEST_SERVER_FAILURE:
				// 登录失败
				Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
			mPullRefreshListView.onRefreshComplete();
		}
	};

	private void signTask(String workItemId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("workItemId", workItemId);
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.SIGN_TASK_URL, handlerSign, Utils.getToken(mAppContext));
	}

	/**
	 * 刷新当前页面
	 */
	protected void refresh() {
		init();
	}

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
				convertView = LayoutInflater.from(mAppContext).inflate(R.layout.layout_item_list_auto_project, null);
				holder.btnSign = (TextView) convertView.findViewById(R.id.btn_sign);
				holder.icon = (ImageView) convertView.findViewById(R.id.img_auto_sign);
				holder.title = (TextView) convertView.findViewById(R.id.work_project_title);
				holder.type = (TextView) convertView.findViewById(R.id.work_project_type);
				holder.content = (TextView) convertView.findViewById(R.id.work_project_typeName);
				holder.person = (TextView) convertView.findViewById(R.id.work_project_empName);
				holder.time = (TextView) convertView.findViewById(R.id.work_auto_createTime);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.icon.setImageResource(R.drawable.icon_auto_sign);
			if (Utils.getRoleId(mAppContext).equals("2")) {
				holder.type.setText(R.string.work_auto_sign);
			}else{
				holder.type.setText(R.string.work_auto_read);
			}
			
			final WorkBean bean = worklist.get(position);
			if (Utils.getRoleId(mAppContext).equals("2")) {
				// 签收按钮
				holder.btnSign.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						String id = bean.getWorkItemId();
						if (!TextUtils.isEmpty(id)) {
							signTask(bean.getWorkItemId());
						}
					}
				});
			}else{
				holder.btnSign.setVisibility(View.GONE);
			}
			
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
