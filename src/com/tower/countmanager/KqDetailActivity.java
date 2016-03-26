package com.tower.countmanager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.CoordinateBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

public class KqDetailActivity extends Activity{

	private static final String TAG = "KqDetailActivity";

	@ViewInject(R.id.subview_title)
    TextView title;
    @ViewInject(R.id.kq_detail_mapview)
    com.baidu.mapapi.map.MapView mMapView;

    BaiduMap mBaiduMap = null;
    String time = "";
    String empId = "";

    List<CoordinateBean> lists = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_kq_detail);
		ViewUtils.inject(this);
		initView();
	}
	private void initView() {
        String name = getIntent().getStringExtra("name");
        time = getIntent().getStringExtra("time");
        empId = getIntent().getStringExtra("empId");
		title.setText(name + " " + time);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMarkerClickListener(listener);
        initData();
	}

	@OnClick(value = {R.id.subview_title_arrow})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            
        }
    }
	
	private void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("emp_id", empId);
        map.put("check_day", time);
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.ATTENDANCE_QUERY_DETAIL_LIST, handlerLoad, Utils.getToken(this));
	}

    private Handler handlerLoad = new Handler() {
        public void handleMessage(android.os.Message msg) {

            Log.e(TAG, (String) msg.obj);
            switch (msg.what) {
            case Const.REQUEST_SERVER_SUCCESS:
                try {
                    JSONObject jsonObject = new JSONObject((String) msg.obj);
                    String success = jsonObject.getString("success");
                    if ("00".equals(success)) {
                        Toast.makeText(KqDetailActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                .show();
                    } else {

                        Gson gson = new Gson();
                        Type listType = new TypeToken<ArrayList<CoordinateBean>>() {}.getType();
                        lists = gson.fromJson(new JSONObject(jsonObject.getString("result")).getString("coordinateList"), listType);
//                        lists = new ArrayList<CoordinateBean>();
//                        CoordinateBean tmp = new CoordinateBean();
//                        tmp.setLongitude(38.856191);//38.856191
//                        tmp.setLatitude(121.514856);
//                        lists.add(tmp);
//                        CoordinateBean tmp1 = new CoordinateBean();
//                        tmp1.setLongitude(38.866191);
//                        tmp1.setLatitude(121.614856);
//                        lists.add(tmp1);
//                        tmp.setLongitude(38.876191);
//                        tmp.setLatitude(121.534856);
//                        lists.add(tmp);

                        if(lists != null && lists.size() > 0) {
                            List<LatLng> points = new ArrayList<LatLng>();
                            for(CoordinateBean bean : lists) {                           	
//                                LatLng point = new LatLng(bean.getLongitude(), bean.getLatitude());
                                LatLng point = new LatLng(bean.getLatitude(), bean.getLongitude());
                                points.add(point);
                                //构建Marker图标
                                BitmapDescriptor bitmap = BitmapDescriptorFactory
                                        .fromResource(R.drawable.kq_detail_location);
                                //构建MarkerOption，用于在地图上添加Marker
                                OverlayOptions option = new MarkerOptions()
                                        .position(point)
                                        .icon(bitmap);
                                //在地图上添加Marker，并显示
                                Marker marker = (Marker) (mBaiduMap.addOverlay(option));
                            }
                            //设置中心点为第一个坐标点位置
                            if (points != null && points.size() > 0) {
                            	LatLng cenpt =  points.get(0);  
                            	mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(cenpt));
							}
                            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                                    .points(points);
                            //添加在地图中
                            Polyline mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
//                            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(new LatLng(lists.get(0).getLongitude(), lists.get(0).getLatitude())));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case Const.REQUEST_SERVER_FAILURE:
                Toast.makeText(KqDetailActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

    BaiduMap.OnMarkerClickListener listener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(Marker marker){

            for(CoordinateBean bean : lists) {
                if(marker.getPosition().latitude == bean.getLongitude()) {
                    View view = KqDetailActivity.this.getLayoutInflater().inflate(R.layout.layout_kq_detail_location, null);
                    TextView time = (TextView)view.findViewById(R.id.item_kq_detail_time);
                    time.setText(bean.getCheck_time());
                    TextView address = (TextView)view.findViewById(R.id.item_kq_detail_address);
                    address.setText(bean.getAddress());
                    TextView lat = (TextView)view.findViewById(R.id.item_kq_detail_lat);
                    lat.setText(bean.getLongitude() + "");
                    TextView lon = (TextView)view.findViewById(R.id.item_kq_detail_lon);
                    lon.setText(bean.getLatitude() + "");

                    //定义用于显示该InfoWindow的坐标点
                    //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                    InfoWindow mInfoWindow = new InfoWindow(view
                            , marker.getPosition(), -70);
                    //显示InfoWindow
                    mBaiduMap.showInfoWindow(mInfoWindow);
                }
            }
            return false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
