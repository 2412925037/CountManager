package com.tower.countmanager;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.HomeManagerBean;
import com.tower.countmanager.bean.HomePleasedBean;
import com.tower.countmanager.bean.HomeTodoTaskBean;
import com.tower.countmanager.bean.HomeWorkerBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.entity.UserEntity;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.XCRoundImageView;

public class SearchManagerActivity extends Activity {
	
	private static final String TAG = "SearchManagerActivity";

    @ViewInject(R.id.home_manager_top_layout)
    private LinearLayout managerTopLayout;//区域经理top Layout
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
    @ViewInject(R.id.home_manager_online_person)
    private TextView onlinePerson;
    private Context mAppContext = null;
	//包裹底部图标的LinearLayout
	@ViewInject(R.id.tabhost)
	private FragmentTabHost mTabHost;
    @ViewInject(R.id.left_user_name)
    private TextView userName;
    @ViewInject(R.id.left_user_eng_name)
    private TextView userId;
    @ViewInject(R.id.left_user_address)
    private TextView userAddress;
    @ViewInject(R.id.left_user_department)
    private TextView userDepartment;
    @ViewInject(R.id.left_user_icon)
    private XCRoundImageView userIcon;
    @ViewInject(R.id.left_draft_layout)
    private RelativeLayout leftDraftLayout;
    @ViewInject(R.id.query_detail_report)
    private LinearLayout queryReport;
    @ViewInject(R.id.left_draft_layout)
    private RelativeLayout draftLayout;

    private SlidingMenu menu = null;
    private String searchDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_manager);
        mAppContext = this.getApplicationContext();
        ViewUtils.inject(this);

        initSlidingMenu();
    }

    @OnClick(value = {R.id.left_logout_btn,R.id.auth_menu, R.id.auth_mission_layout, R.id.auth_kq_layout, R.id.left_user_layout,R.id.query_detail_report})
    public void onButtonClick(View v) {
        switch (v.getId()) {
	        case R.id.left_logout_btn:
	            finish();
	            break;
            case R.id.auth_menu:
                setMenu();
                break;
            case R.id.auth_mission_layout:
                startActivity(new Intent(this, MissionActivity.class));
                break;
            case R.id.auth_kq_layout:
            	Intent intent = new Intent(this, KqActivity.class);
            	intent.putExtra("select", "0");
                startActivity(intent);
                break;
            case R.id.left_user_layout:
                startActivity(new Intent(this, UserInfoActivity.class));
                setMenu();
                break;
            case R.id.query_detail_report:
                startActivity(new Intent(this, ReportFormActivity.class));
                break;
        }
    }

    private void initSlidingMenu() {
        int screenWidth = Utils.getScreenWidth(this);
        menu = new SlidingMenu(this);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidth(10);
        // （阴影效果宽度）
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMode(SlidingMenu.LEFT);
        menu.setMenu(R.layout.layout_left_menu_user);// 设置侧栏的视图
        menu.setBehindWidth((int) (screenWidth * 4 / 5));// 设置左页的宽度
        ViewUtils.inject(this, menu.getMenu());
        menu.setOnOpenListener(new SlidingMenu.OnOpenListener(){

            @Override
            public void onOpen() {
                draftLayout.setVisibility(View.INVISIBLE);
                try {
                    DbUtils db = DbUtils.create(SearchManagerActivity.this, Const.TOWER_DB);
                    UserEntity user = db.findFirst(Selector.from(UserEntity.class).where("empId","=",Utils.getUserId(SearchManagerActivity.this)));
                    userName.setText(user.getEmpName());
                    userId.setText(user.getEmpId());
                    userAddress.setText(user.getProvinceName() + " " + user.getCityName());
                    userDepartment.setText(user.getCompanyName() + " " + user.getOrgName());
                    String path = user.getEmpImg();
                    if(!TextUtils.isEmpty(path)) {

                        if(path.startsWith("http")) {

                            AsyncImageLoader loader = new AsyncImageLoader(userIcon);
                            loader.execute(path);
                        } else {

                            Bitmap rawBitmap0 = BitmapFactory.decodeFile(path);
                            SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(rawBitmap0);
                            userIcon.setImageBitmap(bitmapcache.get());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            
            }
        });
    }

    public void setMenu() {
        if (menu.isMenuShowing()) {
            menu.showContent(true);
        } else {
            menu.showSecondaryMenu(true);
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        initData();
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
                          
                            HomeManagerBean bean = gson.fromJson(jsonObject.getString("result"), HomeManagerBean.class);
                            managerPersonView.setText(String.format(getString(R.string.home_manager_top_person_text), bean.getCountyPersonTotal()));
                            managerOverView.setText(String.format(getString(R.string.home_manager_top_over_text), bean.getCompletePrjTotal()));
                            managerPleasedView.setText(String.format(getString(R.string.home_manager_top_success_text), bean.getCompletePleased()));
                            managerTotalView.setText(String.format(getString(R.string.home_manager_top_mission_text), bean.getPrjTotal()));
                            managerMonth.setText(String.format(getString(R.string.home_manager_month), bean.getMonth()));
                            String num = bean.getOnlinePersonNum();
                            
//                            if(TextUtils.isEmpty(num))
//                                onlinePerson.setText(String.format(getString(R.string.home_manager_top_online), "0"));
//                            else
//                                onlinePerson.setText(String.format(getString(R.string.home_manager_top_online), bean.getOnlinePersonNum()));

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
