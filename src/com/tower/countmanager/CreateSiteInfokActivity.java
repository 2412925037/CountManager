package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 选择基站
 */
public class CreateSiteInfokActivity extends Activity {

	@ViewInject(R.id.subview_title)
	TextView subviewTitle;
    @ViewInject(R.id.subview_title_image)
    TextView subviewRightTitle;
	@ViewInject(R.id.create_site_no_view)
    EditText siteNo;
    @ViewInject(R.id.create_site_name_view)
    EditText siteName;
    @ViewInject(R.id.create_site_latitude_view)
    EditText siteLatitude;
    @ViewInject(R.id.create_site_longitude_view)
    EditText siteLongitude;
    @ViewInject(R.id.create_site_no_title)
    TextView create_site_no_title;
    @ViewInject(R.id.create_site_name_title)
    TextView create_site_name_title;
    @ViewInject(R.id.site_county_title)
    TextView site_county_title;
    @ViewInject(R.id.create_site_county_view)
    TextView siteCounty;
    @ViewInject(R.id.create_site_role_layout)
    LinearLayout siteCountyLayout;
    
    private ProgressDialog dialog = null;
    private String site_sno="";
    private String siteNos;
    private String siteNames;
    private String siteLatitudes;
    private String siteLongitudes;
    private String siteProvinces = "";
    private String siteCitys = "";
    private String siteCountys;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_site_info);
		ViewUtils.inject(this);
		initViews();
		
	}

	public void initViews() {
		site_sno=getIntent().getStringExtra("site_sno").toString();
        subviewTitle.setText(R.string.create_site_title);
        subviewRightTitle.setText(R.string.app_save);
        subviewRightTitle.setVisibility(View.VISIBLE);

        create_site_no_title.setText(Html.fromHtml("站点编号"+"<font color=\"#db4537\"> *</font>"));
        create_site_name_title.setText(Html.fromHtml("站点名称"+"<font color=\"#db4537\"> *</font>"));

        if(Utils.getRoleId(this).equals("1")) {

        } else {
            siteCountyLayout.setVisibility(View.GONE);
        }


        if(TextUtils.isEmpty(site_sno)){

        }else{
    		getMsg();	
        }
    }

    @OnClick(value = { R.id.subview_title_arrow, R.id.subview_title_image})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                this.finish();
                break;
            case R.id.subview_title_image:
                siteNos=siteNo.getText().toString();
                siteNames=siteName.getText().toString();
                siteLatitudes=siteLatitude.getText().toString();
                siteLongitudes=siteLongitude.getText().toString();
               // siteCountys=siteCounty.getText().toString();
                if(TextUtils.isEmpty(siteNos)||TextUtils.isEmpty(siteNames)){
        				Toast.makeText(CreateSiteInfokActivity.this, R.string.app_commit_people_isempty, Toast.LENGTH_SHORT).show();
                	}else{
            	      commitButton();	
            	}
                break;
        }
    }

//    private void createDialog() {
//        if(dialog == null) {
//            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
//            dialog.setCancelable(false);
//        } else
//            dialog.show();
//    }
    
	private void commitButton() {
        //createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("site_sno",site_sno);
        map.put("site_code", siteNos);
        map.put("site_name", siteNames);
        map.put("longitude", siteLatitudes);
        map.put("latitude", siteLongitudes);
        map.put("province", "");
        map.put("city", "");
        map.put("district", "");
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.CREATE_SITE_INFO_URL, handler, Utils.getToken(this));
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(CreateSiteInfokActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else if ("01".equals(success)){
                        Intent intent = new Intent();
                        intent.putExtra("taskSno", jsonObject.getString("result"));
                        intent.putExtra("site_name", siteName.getText().toString());
                        intent.putExtra("longitude", siteLatitude.getText().toString());
                        intent.putExtra("latitude", siteLongitude.getText().toString());
                        intent.putExtra("province",siteProvinces);
                        intent.putExtra("city", siteCitys);
                        intent.putExtra("district", siteCountys);
                        setResult(RESULT_OK, intent);
                        finish();
					} else {
                        Utils.sessionTimeout(CreateSiteInfokActivity.this);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(CreateSiteInfokActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	private void getMsg() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("site_sno",site_sno);
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.GET_SITE_URL, handlers, Utils.getToken(this));
	}

	private Handler handlers = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.e("CreateSiteInfokActivity", (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(CreateSiteInfokActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else if ("01".equals(success)){
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							siteNo.setText(jsonObject2.getString("site_code"));
							siteName.setText(jsonObject2.getString("site_name"));
							siteLatitude.setText(jsonObject2.getString("longitude"));
							siteLongitude.setText(jsonObject2.getString("latitude"));
//                            siteCounty.setText(jsonObject2.getString("district"));
						}
					} else {
                        Utils.sessionTimeout(CreateSiteInfokActivity.this);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(CreateSiteInfokActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
}
