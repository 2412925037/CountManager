package com.tower.countmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.entity.ManagerCountyEntity;
import com.tower.countmanager.util.DateTimePickDialogUtil;
import com.tower.countmanager.util.Utils;

import java.util.List;

public class MissionSearchActivity extends Activity {

	private static final String TAG = "MissionSearchActivity";
	private Context mAppContext;
    @ViewInject(R.id.subview_title)
    TextView title;
    @ViewInject(R.id.mission_search_person_view)
    TextView personView;
    @ViewInject(R.id.mission_search_role_layout)
    LinearLayout roleLayout;
    @ViewInject(R.id.mission_search_county_view)
    TextView countyView;
    @ViewInject(R.id.mission_search_task_report)
    TextView taskReport;
    @ViewInject(R.id.mission_search_task_status)
    TextView taskStatus;
    @ViewInject(R.id.mission_evaluate_ratingbar)
    RatingBar evaluateRatingBar;
    @ViewInject(R.id.mission_search_operate_time_from)
    TextView taskTimefrom;
    @ViewInject(R.id.mission_search_operate_time_to)
    TextView taskTimeto;
    @ViewInject(R.id.mission_task_name)
    EditText taskName;

    private String strProvince = "" ,strCity = "";
    int whichMultipleReport = 0;
    int whichMultipleStatus = 0;
    String[] multipleTask = {"是","否"}; 
    String[] multiPleTaskStatus = {"待签收","执行中","待确认","已完成"};
    String[] multiPleTaskStatus2 = {"执行中","待确认","已完成"};
    int score = 3;
    boolean click1,click2,click3,click4,click5;

    String[] managerCounty = null;
    int managerCountyWhich = 0;
    String[] managerPerson = null;
    String[] managerPersonTemp = null;
    int managerPersonWhich = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mAppContext = this.getApplicationContext();
        setContentView(R.layout.activity_mission_search);
        ViewUtils.inject(this);

        initViews();
       
    }

	private void initViews() {

        title.setText(R.string.mission_title);
        if("2".equals(Utils.getRoleId(mAppContext))){
            roleLayout.setVisibility(View.GONE);
        }else if("1".equals(Utils.getRoleId(mAppContext))){

            try {
                DbUtils db = DbUtils.create(this, Const.TOWER_DB);
                List<ManagerCountyEntity> list = db.findAll(Selector.from(ManagerCountyEntity.class).where("empId", "=", Utils.getUserId(this)));
                if(list != null) {
                    managerCounty = new String[list.size()];
                    managerPerson = new String[list.size()];
                    for(int i = 0; i < list.size(); i++) {
                        managerCounty[i] = list.get(i).getDistrict();
                        managerPerson[i] = list.get(i).getEmpNames();
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }

        }

        evaluateRatingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
				taskStatus.setText("已完成");
				score = (int) rating;
				click4 = true;
			}
		});
    }
	
    @OnClick(value = {R.id.subview_title_arrow, R.id.task_search_button, R.id.mission_search_task_status_layout,
            R.id.mission_search_task_report_layout, R.id.mission_search_operate_time_from, R.id.mission_search_operate_time_to,
            R.id.mission_search_county_layout, R.id.mission_search_person_layout})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.task_search_button:

            	Intent intent = new Intent();
            	intent.setClass(mAppContext,MissionSelectActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putString("task_name", taskName.getText().toString());
            	bundle.putString("province", strProvince);
            	bundle.putString("city", strCity);
            	bundle.putString("county", countyView.getText().toString());
            	bundle.putString("sign_user", personView.getText().toString());
            	bundle.putString("report", taskReport.getText().toString()!=""?""+whichMultipleReport:"");
            	bundle.putString("start_day", taskTimefrom.getText().toString());
            	bundle.putString("end_day", taskTimeto.getText().toString());
            	if(taskStatus.getText().toString().equals(multiPleTaskStatus2[2])){
            		bundle.putString("rating", ""+score);
        		}else{
        			bundle.putString("rating", "");
        		}
            	
            	if("2".equals(Utils.getRoleId(mAppContext))){
            		if(taskStatus.getText().toString().equals(multiPleTaskStatus2[0])){
            			bundle.putString("task_status", "3");
            		}else if(taskStatus.getText().toString().equals(multiPleTaskStatus2[1])){
            			bundle.putString("task_status", "3.5");
            		}else if(taskStatus.getText().toString().equals(multiPleTaskStatus2[2])){
            			bundle.putString("task_status", "4");
            		}else{
            			bundle.putString("task_status", "");
            		}
            		
            		
            	}else{
            		if(taskStatus.getText().toString().equals(multiPleTaskStatus[0])){
            			bundle.putString("task_status", "2");
            		}else if(taskStatus.getText().toString().equals(multiPleTaskStatus[1])){
            			bundle.putString("task_status", "3");
            		}else if(taskStatus.getText().toString().equals(multiPleTaskStatus[2])){
            			bundle.putString("task_status", "3.5");
            		}else if(taskStatus.getText().toString().equals(multiPleTaskStatus[3])){
            			bundle.putString("task_status", "4");
            		}else{
            			bundle.putString("task_status", "");
            		}
            	}
            	intent.putExtras(bundle);
            	startActivity(intent);
                break;
            case R.id.mission_search_task_report_layout:
            	showMultipleTaskReportDialog();
            	click2 = true;
                break;
            case R.id.mission_search_task_status_layout:
            	if("2".equals(Utils.getRoleId(mAppContext))){
            		showMultipleTaskStatusDialog2();
            	}else{
            		showMultipleTaskStatusDialog();
            	}
            	
            	click3 =true;
                break;
            
            case R.id.mission_search_operate_time_from:
            	taskTimefrom.setText(Utils.getData2String());
            	DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
            			MissionSearchActivity.this, taskTimefrom.getText().toString());
    			dateTimePicKDialog.dateTimePicKDialog(taskTimefrom);
                break;
            case R.id.mission_search_operate_time_to:
            	taskTimeto.setText(Utils.getData2String());
            	DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
            			MissionSearchActivity.this, taskTimeto.getText().toString());
    			dateTimePicKDialog1.dateTimePicKDialog(taskTimeto);
                break;

            case R.id.mission_search_county_layout :
                showCountyDialog();
                break;
            case R.id.mission_search_person_layout :
                showPersonDialog();
                break;
        }
    }
    
    public void showMultipleTaskReportDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(multipleTask,
				whichMultipleReport, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichMultipleReport = which;
						dialog.dismiss();
						taskReport.setText(multipleTask[whichMultipleReport]);
					}
				});
		builder.create().show();
	}
    
    public void showMultipleTaskStatusDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(multiPleTaskStatus,
				whichMultipleStatus, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichMultipleStatus = which;
						dialog.dismiss();
						taskStatus.setText(multiPleTaskStatus[whichMultipleStatus]);
					}
				});
		builder.create().show();
	}
    
    public void showMultipleTaskStatusDialog2() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(multiPleTaskStatus2,
				whichMultipleStatus, new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichMultipleStatus = which;
						dialog.dismiss();
						taskStatus.setText(multiPleTaskStatus2[whichMultipleStatus]);
					}
				});
		builder.create().show();
	}

    public void showCountyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(managerCounty,
                managerCountyWhich, new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        managerCountyWhich = which;
                        dialog.dismiss();
                        countyView.setText(managerCounty[managerCountyWhich]);
                    }
                });
        builder.create().show();
    }

    public void showPersonDialog() {
        managerPersonTemp = managerPerson[managerCountyWhich].split(",");
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(managerPersonTemp,
                managerPersonWhich, new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        managerPersonWhich = which;
                        dialog.dismiss();
                        personView.setText(managerPersonTemp[managerPersonWhich]);
                    }
                });
        builder.create().show();
    }
}
