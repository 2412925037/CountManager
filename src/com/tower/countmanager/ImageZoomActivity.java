package com.tower.countmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.AsyncImageLoader;

/**
 * 照片预览页面
 */
public class ImageZoomActivity extends Activity {

	private ViewPager pager;
	private MyPageAdapter adapter;
	private int currentPosition;
	private List<String> mDataList = new ArrayList<String>();

	private RelativeLayout photo_relativeLayout;
	
	List<String> delPositions;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom);
		delPositions = new ArrayList<String>();
//		photo_relativeLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
//		photo_relativeLayout.setBackgroundColor(0x70000000);

		initData();

		// Button photo_bt_exit = (Button) findViewById(R.id.photo_bt_exit);
		// photo_bt_exit.setOnClickListener(new View.OnClickListener() {
		// public void onClick(View v) {
		// finish();
		// }
		// });
		ImageButton photo_bt_del = (ImageButton) findViewById(R.id.photo_bt_del);
		photo_bt_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				delPositions.add(String.valueOf(currentPosition));
				if (mDataList.size() == 1) {
					removeImgs();
//					finish();
					doBack();
				} else {
					removeImg(currentPosition);
					pager.removeAllViews();
					adapter.removeView(currentPosition);
					adapter.notifyDataSetChanged();
				}
			}
		});
		if (getIntent().getBooleanExtra(Const.IS_SHOW_DEL_BTN, false)) {
			photo_bt_del.setVisibility(View.VISIBLE);
		}else {
			photo_bt_del.setVisibility(View.GONE);
		}

		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setOnPageChangeListener(pageChangeListener);

		adapter = new MyPageAdapter(mDataList);
		pager.setAdapter(adapter);
		pager.setCurrentItem(currentPosition);
	}

	private void initData() {
		currentPosition = getIntent().getIntExtra(Const.EXTRA_CURRENT_IMG_POSITION, 0);
		mDataList = getIntent().getStringArrayListExtra(Const.EXTRA_IMAGE_LIST);
	}

	private void removeImgs() {
		mDataList.clear();
	}

	private void removeImg(int location) {
		if (location + 1 <= mDataList.size()) {
			mDataList.remove(location);
		}
	}

	
	
	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		// 重写back按键
		doBack();
	}

	/**
	 * 返回动作，返回前判断数据是否有改变，通知上一页面更新数据
	 */
	private void doBack() {
		if (delPositions.size() > 0) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra("delPositions", (ArrayList<String>) delPositions);
			this.setResult(Activity.RESULT_OK,intent);
		}
		this.finish();
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			currentPosition = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {

		}
	};

	class MyPageAdapter extends PagerAdapter {
		private List<String> dataList = new ArrayList<String>();
		private ArrayList<ImageView> mViews = new ArrayList<ImageView>();

		public MyPageAdapter(List<String> dataList) {
			this.dataList = dataList;
			int size = dataList.size();
			for (int i = 0; i != size; i++) {
				ImageView iv = new ImageView(ImageZoomActivity.this);
				String path = dataList.get(i);
				if (path.contains("http")){
					AsyncImageLoader loader = new AsyncImageLoader(iv);
                    loader.execute(path);
				}
				else
//					ImageDisplayer.getInstance(ImageZoomActivity.this).displayBmp(iv, null, path, false);
					if (!TextUtils.isEmpty(path)) {
						Bitmap bitmap = BitmapFactory.decodeFile(path);
						if (null != bitmap) {
							iv.setImageBitmap(bitmap);
						}
					}
				iv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				mViews.add(iv);
			}
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public Object instantiateItem(View arg0, int arg1) {
			ImageView iv = mViews.get(arg1);
			((ViewPager) arg0).addView(iv);
			return iv;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			if (mViews.size() >= arg1 + 1) {
				((ViewPager) arg0).removeView(mViews.get(arg1));
			}
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		public void removeView(int position) {
			if (position + 1 <= mViews.size()) {
				mViews.remove(position);
			}
		}

	}

//	private void LoadImage(ImageView img, String path) {
//		// 异步加载图片资源
//		AsyncImageLoader async = new AsyncImageLoader(img);
//		// 执行异步加载，并把图片的路径传送过去
//		async.execute(path);
//
//	}
}