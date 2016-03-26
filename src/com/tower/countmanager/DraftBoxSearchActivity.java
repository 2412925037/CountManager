package com.tower.countmanager;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.util.DateTimePickDialogUtil;

public class DraftBoxSearchActivity extends FragmentActivity {

	private static final String TAG = "DraftBoxSearchActivity";
	private Context mAppContext = null;
	@ViewInject(R.id.subview_title)
	TextView title;
    @ViewInject(R.id.subview_title_arrow)
    ImageView titleLeftImg;
    @ViewInject(R.id.tv_content)
    EditText tv_content;
    @ViewInject(R.id.start_time)
    TextView start_time;
    @ViewInject(R.id.end_time)
    TextView end_time;
    int tag = -1;
  //  boolean isUpdate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draftbox_search);
        ViewUtils.inject(this);
        mAppContext = this.getApplicationContext();
        initViews();
    }

    private void initViews() {
    	tag = getIntent().getIntExtra("tag", -1);
        title.setText(R.string.draft_box_search_title);
    }
    
    @OnClick(value = {R.id.subview_title_arrow,R.id.search_button,R.id.layout_start_time,R.id.layout_end_time})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.search_button:
            	Intent i=new Intent();
            	i.putExtra("tag", tag);
            	i.putExtra("task_name",tv_content.getText().toString());
            	i.putExtra("start_day",start_time.getText().toString());
            	i.putExtra("end_day",end_time.getText().toString());
            	i.setClass(mAppContext, DraftBoxSearchMsgActivity.class);
            	startActivity(i);
            	break;
            case R.id.layout_start_time:
            	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
            	String 	startTime= start_time.getText().toString();
            	DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
            			DraftBoxSearchActivity.this, startTime);
    			dateTimePicKDialog.dateTimePicKDialog(start_time);
    		
            	break;
            case R.id.layout_end_time:
            	SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");    
            	String endTime= end_time.getText().toString();
            	DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
            			DraftBoxSearchActivity.this, endTime);
    			dateTimePicKDialog1.dateTimePicKDialog(end_time);
    			
            	break;
        }
    }
}
