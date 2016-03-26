package com.tower.countmanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.MainActivity;
import com.tower.countmanager.R;
import com.tower.countmanager.WorkSearchActivity;
import com.tower.countmanager.util.Utils;

/**
 * 工作tab
 */
public final class WorkFragment extends Fragment {

	private static final String TAG = "WorkFragment";

	@ViewInject(R.id.subview_title)
	TextView title;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;
	@ViewInject(R.id.subview_title_image)
	TextView titleRight;
	@ViewInject(R.id.work_tabhost)
	private FragmentTabHost mTabHost;

	private String titleArray[];
	private Class fragmentArray[] = { AutoSignFragment.class, AutoFragment.class, FinishedFragment.class };
	private int iconArray[] = { R.drawable.work_auto_tab_bg_selector, R.drawable.work_auto_tab_bg_selector,
			R.drawable.work_auto_tab_bg_selector };
	//
	private Context mAppContext = null;
    private MainActivity father = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAppContext = inflater.getContext().getApplicationContext();
		View view = inflater.inflate(R.layout.fragment_work, null);
        ViewUtils.inject(this, view);
        father = (MainActivity) getActivity();
		initViews();
		return view;
	}

	private void initViews() {
		//判断身份，区分显示标题
		if (Utils.getRoleId(mAppContext).equals("2")) {
			titleArray = new String[] { getString(R.string.work_auto_sign), getString(R.string.work_auto),
					getString(R.string.work_finish) };
		}else {
			titleArray = new String[] { getString(R.string.work_auto_read), getString(R.string.work_auto),
					getString(R.string.work_finish) };
		}
		title.setText(R.string.work_my_auto);
//		titleLeftImg.setImageResource(R.drawable.home_title_user_icon);
		titleRight.setText(R.string.work_screening);
		titleRight.setVisibility(View.VISIBLE);
		setupTabView();
		//默认显示tab待办
		mTabHost.setCurrentTab(1);
	}

	@OnClick(value = { R.id.subview_title_arrow, R.id.subview_title_image })
	public void onButtonClick(View v) {

		switch (v.getId()) {
		case R.id.subview_title_arrow:
            father.setMenu();
			break;
		case R.id.subview_title_image:
            int i = mTabHost.getCurrentTab();
			startActivity(new Intent(mAppContext, WorkSearchActivity.class).putExtra("tag", i));
            break;
		}
	}

	/**
	 * 设置顶部图标
	 */
	private void setupTabView() {
		mTabHost.setup(mAppContext, getChildFragmentManager(), R.id.work_auto_realtabcontent);
		mTabHost.getTabWidget().setDividerDrawable(null);

		int count = fragmentArray.length;

		for (int i = 0; i < count; i++) {
			TabHost.TabSpec tabSpec = mTabHost.newTabSpec(titleArray[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragmentArray[i], null);
		}

	}

	private View getTabItemView(int index) {
		LayoutInflater layoutInflater = LayoutInflater.from(mAppContext);
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
