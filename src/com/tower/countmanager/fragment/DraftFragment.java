package com.tower.countmanager.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.AdapterView.OnItemClickListener;
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
import com.tower.countmanager.DraftDetailActivity;
import com.tower.countmanager.DraftReportTaskDetailActivity;
import com.tower.countmanager.R;
import com.tower.countmanager.TaskWaitReadActivity;
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
 * 草稿箱-->起草
 * @author WUSONG
 *
 */
public class DraftFragment extends Fragment {
	private static final String TAG = "DraftFragment";
	@ViewInject(R.id.auto_project_list)
	PullToRefreshListView mPullRefreshListView;
	private ProgressDialog dialog = null;
	ListView listView;
	DraftBoxAdapter pa = null;
	List<DraftBoxBean> list=null;
	private int start = 0;
    private Context mAppContext = null;
    private String task_sno;
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
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				task_sno = list.get(position - 1).getTask_sno();
				 if (Utils.getRoleId(mAppContext).equals("2")) {
					 startActivityForResult(new Intent(mAppContext,DraftReportTaskDetailActivity.class).putExtra("task_sno", task_sno),23);
				 }else{
					 startActivityForResult(new Intent(mAppContext,DraftDetailActivity.class).putExtra("task_sno", task_sno), 22);
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
	/**
	 * 刷新当前页面
	 */
	protected void refresh() {
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
	        map.put("task_state", "1");
	        map.put("task_name", "");
	        map.put("start_day", "");
	        map.put("end_day", "");
	        map.put("start", "0");
	        map.put("limit", Const.MAX_DATA_LIMIT);
	        RequestServerUtils util = new RequestServerUtils();
	        util.load2Server(map, Const.DRAFTBOX_LIST_URL, handler, Utils.getToken(mAppContext));
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
//	        createDialog();
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("task_state", "1");
	        map.put("task_name", "");
	        map.put("start_day", "");
	        map.put("end_day", "");
	        map.put("start", start);
	        map.put("limit", Const.MAX_DATA_LIMIT);
	        RequestServerUtils util = new RequestServerUtils();
	        util.load2Server(map, Const.DRAFTBOX_LIST_URL, moreHandler, Utils.getToken(mAppContext));
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
	    private void addReport(String task_sno) {
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("task_sno",task_sno);
	        RequestServerUtils util = new RequestServerUtils();
	        if(Utils.getRoleId(mAppContext).equals("2")){
	        	util.load2Server(map, Const.ADD_REPORT_URL2, addSupport, Utils.getToken(mAppContext));
	        }else{
	          	util.load2Server(map, Const.ADD_REPORT_URL1, addSupport, Utils.getToken(mAppContext));
	        }
	        
	    }
		private Handler addSupport = new Handler() {
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
                    Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
					break;
				}

			}
		};	
		  private void deleteReport(String task_sno) {
		        Map<String, Object> map = new HashMap<String, Object>();
		        map.put("task_sno",task_sno);
		        RequestServerUtils util = new RequestServerUtils();
		        util.load2Server(map, Const.DELETE_REPORT_URL, deleteSupport, Utils.getToken(mAppContext));
		    }
			private Handler deleteSupport = new Handler() {
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
	                    Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
						break;
					}

				}
			};	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 22:
					refresh();
			case 23:
				refresh();
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
				
				holder.btn_confirm=(TextView)convertView.findViewById(R.id.btn_confirm);
				holder.btn_delete=(TextView)convertView.findViewById(R.id.btn_delete);
				convertView.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			final DraftBoxBean bean = list.get(position);
			holder.work_project_name.setText(bean.getTask_name());
			holder.tv_style.setText(bean.getTask_type_name()+"--"+bean.getTask_type2());
			holder.tv_waitsure.setText(R.string.draft_box_draft);
			holder.work_people_name.setText(bean.getOperator());
			holder.work_auto_sign_time.setText(bean.getOperate_time());
			if (Utils.getRoleId(mAppContext).equals("2")) {
				holder.btn_confirm.setText(R.string.draft_report);
			}else{
				holder.btn_confirm.setText(R.string.draft_issued);
			}
			holder.btn_confirm.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String task_sno = bean.getTask_sno();
					if (!TextUtils.isEmpty(task_sno)) {
						addReport(bean.getTask_sno());
					}
				}
			});
			holder.btn_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					String task_sno = bean.getTask_sno();
					if (!TextUtils.isEmpty(task_sno)) {
						deleteReport(bean.getTask_sno());
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView work_project_name;
			TextView tv_style;
			TextView tv_waitsure;
			TextView work_people_name;
			TextView work_auto_sign_time;
			TextView btn_confirm;
			TextView btn_delete;
		}

	}
}
