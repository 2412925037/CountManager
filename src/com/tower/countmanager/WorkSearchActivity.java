package com.tower.countmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.util.DateTimePickDialogUtil;
import com.tower.countmanager.util.Utils;

public class WorkSearchActivity extends Activity {

	@ViewInject(R.id.subview_title)
	private TextView title;
    @ViewInject(R.id.work_search_creater_layout)
    private LinearLayout createrLayout;
    @ViewInject(R.id.work_search_name_view)
    private EditText nameView;
    @ViewInject(R.id.work_search_type_view)
    private TextView typeView;
    @ViewInject(R.id.work_search_time_from)
    private TextView timeFrom;
    @ViewInject(R.id.work_search_time_to)
    private TextView timeTo;

    ProgressDialog dialog = null;
    private String[] taskTypes;
    int whichTaskType = 0;
    String taskTypeNum = "";
    int tag = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_search);
		ViewUtils.inject(this);

        init();
	}

    private void init() {
        title.setText(R.string.mission_title);
        tag = getIntent().getIntExtra("tag", -1);

        createrLayout.setVisibility(View.GONE);
        taskTypes = getResources().getStringArray(R.array.task_type);
//        timeFrom.setText(Utils.getData2String());
//        timeTo.setText(Utils.getData2String());
    }

	@OnClick(value = {R.id.work_search_button, R.id.subview_title_arrow, R.id.work_search_type_layout,
            R.id.work_search_time_to, R.id.work_search_time_from})
	public void onButtonClick(View v){
		
		switch(v.getId()) {
            case R.id.work_search_button :
                String tmp = typeView.getText().toString();
                if(getString(R.string.work_search_create_site).equals(tmp)){
                    taskTypeNum="T1";
                }else if(getString(R.string.work_search_stock).equals(tmp)){
                    taskTypeNum="T2";
                }else if(getString(R.string.work_search_managerment).equals(tmp)){
                    taskTypeNum="T3";
                }else if(getString(R.string.work_search_other).equals(tmp)){
                    taskTypeNum="T4";
                }

                Intent intent = new Intent(this, WorkSearchResultActivity.class);
                intent.putExtra("tag", tag);
                intent.putExtra("title", nameView.getText().toString());
                intent.putExtra("typeId", taskTypeNum);
                intent.putExtra("startDate", timeFrom.getText().toString());
                intent.putExtra("endDate", timeTo.getText().toString());

                startActivity(intent);
			    break;
            case R.id.subview_title_arrow :
                finish();
                break;
            case R.id.work_search_type_layout :
                showTaskTypeDialog();
                break;
            case R.id.work_search_time_from :
                String str1 = timeFrom.getText().toString();
                DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
                        this, str1);
                dateTimePicKDialog1.dateTimePicKDialog(timeFrom);
                break;
            case R.id.work_search_time_to :
                String str = timeTo.getText().toString();
                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        this, str);
                dateTimePicKDialog.dateTimePicKDialog(timeTo);
                break;

		}
	}

    public void showTaskTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(taskTypes, whichTaskType,
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        whichTaskType = which;
                        dialog.dismiss();
                        typeView.setText(taskTypes[whichTaskType]);
                    }
                });
        builder.create().show();
    }

}
	
