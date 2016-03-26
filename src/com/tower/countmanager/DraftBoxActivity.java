package com.tower.countmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.fragment.AutoConfirmFragment;
import com.tower.countmanager.fragment.DraftFragment;
import com.tower.countmanager.util.Utils;
/**
 * 草稿箱
 * @author WUSONG
 *
 */
public class DraftBoxActivity extends FragmentActivity {

	private static final String TAG = "DraftBoxFragment";
	private Context mAppContext = null;
	@ViewInject(R.id.subview_title)
	TextView title;
    @ViewInject(R.id.subview_title_arrow)
    ImageView titleLeftImg;
    @ViewInject(R.id.subview_title_image)
    TextView titleRight;
    @ViewInject(R.id.draft_box_tab)
    private FragmentTabHost mTabHost;
//
    private String titleArray[];
    private Class fragmentArray[] = {
            DraftFragment.class,
            AutoConfirmFragment.class};
    private int iconArray[] = {
            R.drawable.work_auto_tab_bg_selector,
            R.drawable.work_auto_tab_bg_selector};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft_box);
        ViewUtils.inject(this);
        mAppContext = this.getApplicationContext();
        initViews();
    }

    private void initViews() {
    	//判断身份，区分显示标题
    	if (Utils.getRoleId(mAppContext).equals("2")) {
            	titleArray = new String[] { getString(R.string.draft_box_draft), getString(R.string.draft_box_auto_confirm)};
    		}else {
    			titleArray = new String[] { getString(R.string.draft_box_draft), getString(R.string.work_auto_sign)};
    		}
        title.setText(R.string.draft_box_title);
        setupTabView();
    }
    
    @OnClick(value = {R.id.subview_title_arrow,R.id.subview_title_filter,R.id.subview_title_add})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.subview_title_filter:
            	//TODO  筛选
            	int i = mTabHost.getCurrentTab();
            	Intent intent=new Intent();
            	intent.putExtra("tag", i);
            	intent.setClass(mAppContext, DraftBoxSearchActivity.class);
				startActivity(intent);
                break;
            case R.id.subview_title_add:
            	//TODO  新增
            	if (Utils.getRoleId(mAppContext).equals("2")) {
            		startActivity(new Intent(mAppContext, ReportTaskActivity.class));
        		}else {
        			startActivity(new Intent(mAppContext, AssignmentTaskActivity.class));     
        		}
                break;
        }
    }

    /**
     * 设置顶部图标
     */
    private void setupTabView() {
        mTabHost.setup(this, getSupportFragmentManager(), R.id.work_auto_realtabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			TabHost.TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}

    }
    private View getTabItemView(int index) {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View view = layoutInflater.inflate(R.layout.tab_top_text, null);
		try {

			TextView tabTV = (TextView) view.findViewById(R.id.auto_tab_tv);
			tabTV.setText(titleArray[index]);
			tabTV.setBackgroundResource(iconArray[index]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}
}
