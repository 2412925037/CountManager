package com.tower.countmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
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

public class KqSearchActivity extends Activity {

	private static final String TAG = "KqSearchActivity";

	@ViewInject(R.id.subview_title)
	TextView title;
    @ViewInject(R.id.kq_role)
    LinearLayout roleLayout;
    @ViewInject(R.id.kq_seach_county_view)
    TextView countyView;
    @ViewInject(R.id.kq_seach_person_view)
    TextView personNameView;
    @ViewInject(R.id.kq_search_time_from)
    TextView timeFrom;
    @ViewInject(R.id.kq_search_time_to)
    TextView timeTo;

    private String strProvince = "", strCity = "";

    String[] managerCounty = null;
    int managerCountyWhich = 0;
    String[] managerPerson = null;
    String[] managerPersonTemp = null;
    int managerPersonWhich = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kq_search);
        ViewUtils.inject(this);
        initViews();
        init();
    }

    private void init() {
        if("2".equals(Utils.getRoleId(this))) {
            roleLayout.setVisibility(View.GONE);
        } else {
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
       
	}

	private void initViews() {
        title.setText(R.string.kq_title);
        

    }
    @OnClick(value = {R.id.subview_title_arrow,R.id.kq_search_button,R.id.kq_search_time_from,R.id.kq_search_time_to,
            R.id.kq_search_county_layout, R.id.kq_search_person_layout})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.kq_search_time_from:
            	timeFrom.setText(Utils.getData2String());
    	    	String str = timeFrom.getText().toString();
            	DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
            			KqSearchActivity.this, str);
    			dateTimePicKDialog.dateTimePicKDialog(timeFrom);
                break;
            case R.id.kq_search_time_to:
            	
                timeTo.setText(Utils.getData2String());
    	    	String str1 = timeTo.getText().toString();
            	DateTimePickDialogUtil dateTimePicKDialog1 = new DateTimePickDialogUtil(
            			KqSearchActivity.this, str1);
    			dateTimePicKDialog1.dateTimePicKDialog(timeTo);
                break;
            case R.id.kq_search_button:
            	Intent intent = new Intent();
            	intent.setClass(this,KqSelectActivity.class);
            	Bundle bundle = new Bundle();
            	bundle.putString("emp_name", personNameView.getText().toString());
            	bundle.putString("check_start_time", timeFrom.getText().toString());
            	bundle.putString("check_end_time", timeTo.getText().toString());
            	bundle.putString("province", strProvince);
            	bundle.putString("city", strCity);
            	bundle.putString("district", countyView.getText().toString());
            	intent.putExtras(bundle);
            	startActivity(intent);
                break;
            case R.id.kq_search_county_layout :
                showCountyDialog();
                break;
            case R.id.kq_search_person_layout :
                showPersonDialog();
                break;
        }
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
                        personNameView.setText(managerPersonTemp[managerPersonWhich]);
                    }
                });
        builder.create().show();
    }
}
