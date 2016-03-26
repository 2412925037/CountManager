package com.tower.countmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.entity.ManagerCountyEntity;
import com.tower.countmanager.entity.UserEntity;
import com.tower.countmanager.fragment.HomeFragment;
import com.tower.countmanager.fragment.WorkFragment;
import com.tower.countmanager.service.LocationService;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.XCRoundImageView;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;


public class MainActivity extends FragmentActivity {

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

	//底部图标名称
	private String titleArray[] = {
            "Home",
            "Work"};
	private Class fragmentArray[] = {
			HomeFragment.class,
			WorkFragment.class};
	private int iconArray[] = {
			R.drawable.home_bottom_icon_home,
			R.drawable.home_bottom_icon_work};

	private SlidingMenu menu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);

        setupTabView();
        initSlidingMenu();

        startService(new Intent(this, LocationService.class));
	}

	/**
	 * 设置底部图标
	 */
	private void setupTabView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }

    }

    /**
     * 初始化底部图标
     */
	private View getTabItemView(int index) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.tab_bottom_nav, null);
        try {

            ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
            imageView.setImageResource(iconArray[index]);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public FragmentTabHost getTab() {
        return mTabHost;
    }

    /**
     * 监听back键退出应用
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //setMenu();
            if (menu.isMenuShowing())
                menu.showContent(true);
            else
                createLogoutAlert();
            return false;
        }
        else
            return super.onKeyDown(keyCode, event);
    }

    private void createLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton(R.string.app_alert_button_ok, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                setResult(99);
                finish();
            }

        })
                .setNegativeButton(R.string.app_alert_button_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                })
                .setMessage(getText(R.string.app_alert_logout_message).toString())
                .setTitle(R.string.app_alert_logout_title)
                .setCancelable(true);
        AlertDialog a = builder.create();
        a.show();
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
		menu.setOnOpenListener(new OnOpenListener(){

			@Override
			public void onOpen() {
                try {
                    DbUtils db = DbUtils.create(MainActivity.this, Const.TOWER_DB);
                    UserEntity user = db.findFirst(Selector.from(UserEntity.class).where("empId","=",Utils.getUserId(MainActivity.this)));
                    userName.setText(user.getEmpName());
                    userId.setText(user.getEmpId());
                    userAddress.setText(user.getProvinceName() + " " + user.getCityName());
                    userDepartment.setText(user.getOrgName());
                    String path = Utils.getUserPhoto(MainActivity.this);
                    File tmp = new File(path);
                    if(tmp.exists()) {
                        Bitmap rawBitmap0 = BitmapFactory.decodeFile(path);
                        SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(rawBitmap0);
                        userIcon.setImageBitmap(bitmapcache.get());
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

    @OnClick(value = {R.id.left_logout_btn, R.id.left_draft_layout, R.id.left_user_layout})
    public void onButtonClick(View v) {

        switch (v.getId()) {
            case R.id.left_logout_btn:
                finish();
//                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
                break;
            case R.id.left_draft_layout:
                startActivity(new Intent(this, DraftBoxActivity.class));
                setMenu();
                break;
            case R.id.left_user_layout:
                startActivity(new Intent(this, UserInfoActivity.class));
                setMenu();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.deletePicFromMyImage(this);

        try {
            DbUtils db = DbUtils.create(this, Const.TOWER_DB);
            List<ManagerCountyEntity> list = db.findAll(Selector.from(ManagerCountyEntity.class).where("empId", "=", Utils.getUserId(this)));
            if(list != null) {
                db.deleteAll(list);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
	
