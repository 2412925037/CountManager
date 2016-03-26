package com.tower.countmanager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.LoginBean;
import com.tower.countmanager.bean.ManagerCounty;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.entity.ManagerCountyEntity;
import com.tower.countmanager.entity.UserEntity;
import com.tower.countmanager.service.LocationService;
import com.tower.countmanager.util.FileUtil;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

public class LoginActivity extends Activity {

	private static final String TAG = "LoginActivity";
	
	@ViewInject(R.id.login_count)
	private EditText userIdEditText;
	@ViewInject(R.id.login_pwd)
	private EditText userPwdEditText;

	private ProgressDialog dialog = null;
    String userId = "";
    String userPwd = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);
	}

	@OnClick(value = {R.id.login_commit_button})
	public void onButtonClick(View v){
		
		switch(v.getId()) {
		case R.id.login_commit_button :
			userId = userIdEditText.getText().toString();
			userPwd = userPwdEditText.getText().toString();
			if(TextUtils.isEmpty(userId)) {
				Toast.makeText(this, R.string.login_userid_error, Toast.LENGTH_SHORT).show();
			} else if(TextUtils.isEmpty(userPwd)) {
				Toast.makeText(this, R.string.login_userpwd_error, Toast.LENGTH_SHORT).show();
			} else if(!Utils.isConnect(this)){
				Toast.makeText(this, R.string.app_connecting_network_no_connect, Toast.LENGTH_SHORT).show();
			} else {
				commitButton();
			}
			break;
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 99 && resultCode == 99)
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        stopService(new Intent(this, LocationService.class));
    }

    private void createDialog() {
        if(dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

	/**
	 * 提交登录
	 */
	private void commitButton() {
		createDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("empAccount", userId);
		map.put("password", userPwd);
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(map, Const.LOGIN_URL, handler);
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			
			if(dialog != null)
				dialog.dismiss();
			Log.e(TAG, msg.obj.toString());
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject(msg.obj.toString());
					String success = jsonObject.getString("success");
					if("00".equals(success)) {
                        Toast.makeText(LoginActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					} else {
                        Gson gson = new Gson();
                        LoginBean bean = gson.fromJson(jsonObject.getString("result"), LoginBean.class);


                        String path = bean.getEmpImg();
                        File backup = new File(FileUtil.getUserIconDir(LoginActivity.this), "/" + String.valueOf(System.currentTimeMillis()) + ".png");
                        if(!TextUtils.isEmpty(path)) {
                            RequestServerUtils util = new RequestServerUtils();
                            util.downloadFile(bean.getEmpImg(), backup.getPath());
                        }

                        getSharedPreferences(Const.SHARED_PREF, Activity.MODE_PRIVATE).edit()
                        .putString(Const.USER_ID, bean.getEmpId())
                        .putString(Const.TOKEN, bean.getToken())
                        .putString(Const.ROLE_ID, bean.getRoleId())
                        .putString(Const.USER_PHOTO, backup.getPath())
                        .commit();

                        DbUtils db = DbUtils.create(LoginActivity.this, Const.TOWER_DB);
                        UserEntity user = db.findFirst(Selector.from(UserEntity.class).where("empId","=",bean.getEmpId()));
                        if(user == null) {
                            user = new UserEntity();
                            user.setEmpId(bean.getEmpId());
                            user.setEmpName(bean.getEmpName());
                            user.setEmpImg(bean.getEmpImg());
                            user.setDutyName(bean.getDutyName());
                            user.setCompanyId(bean.getCompanyId());
                            user.setCompanyName(bean.getCompanyName());
                            user.setOrgId(bean.getOrgId());
                            user.setOrgName(bean.getOrgName());
                            user.setProvinceName(bean.getProvinceName());
                            user.setCityName(bean.getCityName());
                            user.setDistrictName(bean.getDistrictName());
                            db.save(user);
                        } else {
                            user.setEmpId(bean.getEmpId());
                            user.setEmpName(bean.getEmpName());
                            user.setEmpImg(bean.getEmpImg());
                            user.setDutyName(bean.getDutyName());
                            user.setCompanyId(bean.getCompanyId());
                            user.setCompanyName(bean.getCompanyName());
                            user.setOrgId(bean.getOrgId());
                            user.setOrgName(bean.getOrgName());
                            user.setProvinceName(bean.getProvinceName());
                            user.setCityName(bean.getCityName());
                            user.setDistrictName(bean.getDistrictName());
                            db.saveOrUpdate(user);
                        }

                        List<ManagerCounty> list = bean.getDistrictNames();
                        if(list != null && list.size() > 0) {
                            for(ManagerCounty tmp : list) {
                                ManagerCountyEntity county = new ManagerCountyEntity();
                                county.setEmpNames(tmp.getEmpNames());
                                county.setDistrict(tmp.getDistrict());
                                county.setEmpId(bean.getEmpId());
                                db.save(county);
                            }
                        }
                        if(Utils.getRoleId(LoginActivity.this).equals("3")){
                        	startActivityForResult(new Intent(LoginActivity.this, SearchManagerActivity.class), 99);
                		}else{
                            startActivityForResult(new Intent(LoginActivity.this, MainActivity.class), 99);
                		}
                        //检查更新版本
                        checkVersionCode(bean.getAppDesc(),bean.getAppVersion(),bean.getAppUrl());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			case Const.REQUEST_SERVER_FAILURE: 
				//登录失败
				Toast.makeText(LoginActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private void checkVersionCode(String tips,String versionCode,String url) {
		if (!TextUtils.isEmpty(versionCode)) {
			int newCode = Integer.parseInt(versionCode);
			int oldCode = Utils.getVersionCode(this);
			if (newCode > oldCode) {
				//提示更新版本
		        sendNotification(tips,url);
			}
		}
	}
	
	public void sendNotification(String title,String url){
		NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle(title)
                .setContentText(url).setDefaults(Notification.DEFAULT_ALL)
                .setTicker(title);
        
        //Notification.Builder builder=new Notification.Builder(this);
        mBuilder.setAutoCancel(true);
        mBuilder.setLights(Color.BLUE, 500, 500);
        long[] pattern = {500,500,500,500,500,500};
        mBuilder.setVibrate(pattern);
        
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);
        
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }
	
}
	
