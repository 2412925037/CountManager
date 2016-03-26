package com.tower.countmanager.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.AssignmentTaskActivity;
import com.tower.countmanager.KqActivity;
import com.tower.countmanager.MainActivity;
import com.tower.countmanager.MissionActivity;
import com.tower.countmanager.R;
import com.tower.countmanager.ReportFormActivity;
import com.tower.countmanager.ReportTaskActivity;
import com.tower.countmanager.TaskProcessingEvaluationActvity;
import com.tower.countmanager.TodoActivity;
import com.tower.countmanager.bean.HomeManagerBean;
import com.tower.countmanager.bean.HomePleasedBean;
import com.tower.countmanager.bean.HomeTodoTaskBean;
import com.tower.countmanager.bean.HomeWorkerBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.YMPickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HomeFragment extends Fragment {

	private static final String TAG = "HomeFragment";

    @ViewInject(R.id.home_worker_top_layout)
    private LinearLayout workerTopLayout;//县域top Layout
    @ViewInject(R.id.home_worker_success_layout)
    private LinearLayout workerLayout;//县域满意度排行
    @ViewInject(R.id.home_manager_top_layout)
    private LinearLayout managerTopLayout;//区域经理top Layout
    @ViewInject(R.id.home_mission_button)
    private TextView missionBtn;
    @ViewInject(R.id.home_worker_receive_task)
    private TextView workerReceive;
    @ViewInject(R.id.home_worker_complete_task)
    private TextView workerComplete;
    @ViewInject(R.id.home_worker_task_pleased)
    private TextView workerTaskPleased;
    @ViewInject(R.id.home_worker_my_ranking)
    private TextView workerPleasedRanking;
    @ViewInject(R.id.home_worker_ranking_one)
    private TextView workerNameOne;
    @ViewInject(R.id.home_worker_ranking_two)
    private TextView workerNameTwo;
    @ViewInject(R.id.home_worker_ranking_three)
    private TextView workerNameThree;
    @ViewInject(R.id.home_worker_ranking_percent_one)
    private TextView workerPercentOne;
    @ViewInject(R.id.home_worker_ranking_percent_two)
    private TextView workerPercentTwo;
    @ViewInject(R.id.home_worker_ranking_percent_three)
    private TextView workerPercentThree;
    @ViewInject(R.id.home_todo_title)
    private TextView todoTitle;
    @ViewInject(R.id.home_todo_content)
    private TextView todoContent;
    @ViewInject(R.id.home_todo_person)
    private TextView todoName;
    @ViewInject(R.id.home_todo_time)
    private TextView todoTime;
    @ViewInject(R.id.home_todo_number)
    private TextView todoTaskTotal;
    @ViewInject(R.id.home_manager_month)
    private TextView managerMonth;
    @ViewInject(R.id.home_manager_top_person)
    private TextView managerPersonView;
    @ViewInject(R.id.home_manager_top_over)
    private TextView managerOverView;
    @ViewInject(R.id.home_manager_top_pleased)
    private TextView managerPleasedView;
    @ViewInject(R.id.home_manager_top_total)
    private TextView managerTotalView;
    @ViewInject(R.id.home_task_choice_time)
    private TextView topTaskTime;
    @ViewInject(R.id.home_manager_online_person)
    private TextView onlinePerson;

	private Context mAppContext = null;
	private MainActivity father = null;

	private String workItemId = "";
	private String searchDate = "";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	mAppContext = inflater.getContext().getApplicationContext();
    	View view = inflater.inflate(R.layout.fragment_home, null);
    	ViewUtils.inject(this, view);

        init();
    	father = (MainActivity)getActivity();
        return view;
    }

    private void init() {
        String roleId = Utils.getRoleId(mAppContext);
        if("1".equals(roleId)) {
            workerTopLayout.setVisibility(View.GONE);
            workerLayout.setVisibility(View.GONE);
            missionBtn.setText(R.string.home_manager_button_text);
        } else if("2".equals(roleId)) {
            managerTopLayout.setVisibility(View.GONE);
            topTaskTime.setText(Utils.getLastMonth());
        }
    }

    @OnClick(value = {R.id.home_menu, R.id.home_bottom_kq_layout, R.id.home_bottom_mission_layout,
            R.id.home_first_todo_layout, R.id.home_mission_button, R.id.home_todo_number, R.id.home_manager_report_detail,R.id.choose_search_month})
    public void onButtonClick(View v){

        switch(v.getId()) {
            case R.id.home_bottom_kq_layout :
                startActivity(new Intent(mAppContext, KqActivity.class));
                break;
            case R.id.home_bottom_mission_layout :
                startActivity(new Intent(mAppContext, MissionActivity.class));
                break;
            case R.id.home_menu :
            	father.setMenu();
                break;
            case R.id.home_first_todo_layout :
                if(!TextUtils.isEmpty(todoContent.getText().toString())) {

                    if(Utils.getRoleId(mAppContext).equals("1")) {
                        startActivity(new Intent(mAppContext, TaskProcessingEvaluationActvity.class).putExtra("workId",
                                workItemId).putExtra("type", "auto"));
                    } else {
                        Intent i = new Intent(mAppContext, TodoActivity.class);
                        i.putExtra("workId", workItemId);
                        startActivity(i);
                    }
                }
                break;
            case R.id.home_todo_number :
                father.getTab().setCurrentTab(1);
                break;
            case R.id.home_mission_button :
                if(getString(R.string.home_worker_button_text).equals(missionBtn.getText().toString()))
                    startActivity(new Intent(mAppContext, ReportTaskActivity.class));
                else
                    startActivity(new Intent(mAppContext, AssignmentTaskActivity.class));
                break;
            case R.id.home_manager_report_detail :
                startActivity(new Intent(mAppContext, ReportFormActivity.class));
                break;
            case R.id.choose_search_month:
            	chooseSearchDate();
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }
    /**
     * 选择年月
     */
	public void chooseSearchDate() {
		final Calendar cal = Calendar.getInstance();
		DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
				// Calendar月份是从0开始,所以month要加1
				String time = year + "-" + (month + 1);
				topTaskTime.setText(time);
				searchDate = time;
				//刷新数据
				initData();
			}
		};
		YMPickerDialog mDialog = new YMPickerDialog(father, dateListener, cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH));
		mDialog.show();
		DatePicker dp = mDialog.findDatePicker((ViewGroup) mDialog.getWindow().getDecorView());
		if (dp != null) {
			((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
		}
	}
    
    
    private void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        RequestServerUtils util = new RequestServerUtils();
        map.put("searchDate", searchDate);
        util.load2Server(map, Const.HOME_URL, handler, Utils.getToken(mAppContext));
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            Log.e(TAG, msg.obj.toString());
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String success = jsonObject.getString("success");
                        if("00".equals(success)) {
                            Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                        } else if("01".equals(success)){
                            Gson gson = new Gson();
                            if("2".equals(Utils.getRoleId(mAppContext))) {

                                HomeWorkerBean bean = gson.fromJson(jsonObject.getString("result"), HomeWorkerBean.class);
                                workerReceive.setText(String.format(getString(R.string.home_worker_task), bean.getReceiveTaskTotal()));
                                workerComplete.setText(String.format(getString(R.string.home_worker_task), bean.getCompleteTaskTotal()));
                                workerTaskPleased.setText(bean.getTaskPleased());
                                workerPleasedRanking.setText(String.format(getString(R.string.home_worker_success_text), bean.getPleasedRanking()));
                                List<HomePleasedBean> pleasedBean = bean.getPleasedList();
                                if(pleasedBean != null && pleasedBean.size() > 0) {
                                    for(int i = 0; i < pleasedBean.size(); i++) {
                                        if(0 == i) {
                                            workerNameOne.setText(pleasedBean.get(0).getEmpName());
                                            workerPercentOne.setText(pleasedBean.get(0).getPercent());
                                        } else if(1 == i) {
                                            workerNameTwo.setText(pleasedBean.get(1).getEmpName());
                                            workerPercentTwo.setText(pleasedBean.get(1).getPercent());
                                        } else if(2 == i) {
                                            workerNameThree.setText(pleasedBean.get(2).getEmpName());
                                            workerPercentThree.setText(pleasedBean.get(2).getPercent());
                                        }

                                    }
                                }
                                todoTaskTotal.setText(String.format(getString(R.string.home_todo_number_text), bean.getTaskTotal()));
                                HomeTodoTaskBean taskBean = bean.getTaskInfo();
                                todoTitle.setText(taskBean.getTitle());
                                todoContent.setText(taskBean.getTypeName());
                                todoName.setText(taskBean.getSendEmpName());
                                todoTime.setText(taskBean.getCreateTime());
                                workItemId = taskBean.getWorkItemId();
                            } else if("1".equals(Utils.getRoleId(mAppContext))) {
                                HomeManagerBean bean = gson.fromJson(jsonObject.getString("result"), HomeManagerBean.class);
                                managerPersonView.setText(String.format(getString(R.string.home_manager_top_person_text), bean.getCountyPersonTotal()));
                                managerOverView.setText(String.format(getString(R.string.home_manager_top_over_text), bean.getCompletePrjTotal()));
                                managerPleasedView.setText(String.format(getString(R.string.home_manager_top_success_text), bean.getCompletePleased()));
                                managerTotalView.setText(String.format(getString(R.string.home_manager_top_mission_text), bean.getPrjTotal()));
                                todoTaskTotal.setText(String.format(getString(R.string.home_todo_number_text), bean.getTaskTotal()));
                                HomeTodoTaskBean taskBean = bean.getTaskInfo();
                                todoTitle.setText(taskBean.getTitle());
                                todoContent.setText(taskBean.getTypeName());
                                todoName.setText(taskBean.getSendEmpName());
                                todoTime.setText(taskBean.getCreateTime());
                                workItemId = taskBean.getWorkItemId();
                                managerMonth.setText(String.format(getString(R.string.home_manager_month), bean.getMonth()));
                                String num = bean.getOnlinePersonNum();
                                if(TextUtils.isEmpty(num))
                                    onlinePerson.setText(String.format(getString(R.string.home_manager_top_online), "0"));
                                else
                                    onlinePerson.setText(String.format(getString(R.string.home_manager_top_online), bean.getOnlinePersonNum()));
                            }


                        } else
                            Utils.sessionTimeout(mAppContext);
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
}
