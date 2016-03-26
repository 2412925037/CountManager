package com.tower.countmanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.LocationUtil;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service{

    private static final String TAG = "LocationService";

    LocationUtil u = null;
    String latitude = "";
    String longitude = "";
    String address = "";

    private Timer myTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        u = new LocationUtil(this);
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                u.getLocation(new LocationUtil.LocationCallBack() {
                    @Override
                    public void onSuccess(BDLocation location) {
                        handler.sendEmptyMessage(1234); // 通过Handler切换图片
                        latitude = location.getLatitude() + "";
                        longitude = location.getLongitude() + "";
                        address = location.getAddrStr();
                    }
                });
            }
        }, 1000, 1000 * 60 * 30);//3秒切换图片

    }

    private void commitData() {
        new Thread() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("longitude", longitude);
                map.put("latitude", latitude);
                map.put("address", address);
                RequestServerUtils util = new RequestServerUtils();
                util.load2Server(map, Const.UPLOAD_LOCATION, handler, Utils.getToken(getApplicationContext()));
            }
        }.start();

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject((String) msg.obj);
                        String success = jsonObject.getString("success");
                        Log.e(TAG, "upload location: " + success);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    break;
                case 1234 :
                    commitData();
                    break;
            }
        }
    };
}
