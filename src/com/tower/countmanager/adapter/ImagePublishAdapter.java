package com.tower.countmanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tower.countmanager.R;
import com.tower.countmanager.bean.ImageItem;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.ImageDisplayer;
import com.tower.countmanager.view.RoundAngleImageView;

public class ImagePublishAdapter extends BaseAdapter {
	private List<ImageItem> mDataList = new ArrayList<ImageItem>();
	private Context mContext;
	private boolean isShowAdd = true;
	private int maxSize = 0;
	public ImagePublishAdapter(Context context, List<ImageItem> dataList,boolean isShowAdd,int maxSize) {
		this.mContext = context;
		this.mDataList = dataList;
		this.isShowAdd = isShowAdd;
		this.maxSize = maxSize;
	}

	public int getCount() {
		if (isShowAdd) {
			// 多返回一个用于展示添加图标
			if (mDataList == null) {
				return 1;
			} else if (mDataList.size() == maxSize) {
				return maxSize;
			} else {
				return mDataList.size() + 1;
			}
		}else {
			return mDataList.size();
		}
	}

	public Object getItem(int position) {
		if (isShowAdd) {
			if (mDataList != null && mDataList.size() == maxSize) {
				return mDataList.get(position);
			}

			else if (mDataList == null || position - 1 < 0
					|| position > mDataList.size()) {
				return null;
			} else {
				return mDataList.get(position - 1);
			}
		}else {
			return mDataList.get(position);
		}
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {
		// 所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
		convertView = View.inflate(mContext, R.layout.item_publish, null);
		RoundAngleImageView imageIv = (RoundAngleImageView) convertView
				.findViewById(R.id.item_grid_image);

		if (isShowAddItem(position) && isShowAdd) {
			imageIv.setImageResource(R.drawable.icon_add_photo);

		} else {
			String sourcePath = mDataList.get(position).getDoc_path();
			if (!TextUtils.isEmpty(sourcePath)) {
				if (sourcePath.contains("http")){
//					LoadImage(imageIv, sourcePath);
//					DownloadImageTask task = new DownloadImageTask(mContext, imageIv);
//					task.execute(sourcePath);
					AsyncImageLoader loader = new AsyncImageLoader(imageIv);
                    loader.execute(sourcePath);
				}
				else
					ImageDisplayer.getInstance(mContext).displayBmp(imageIv, "",
							sourcePath);
			}else {
//				imageIv.setImageResource(R.drawable.icon_photo_default);
			}
		}

		return convertView;
	}

	private boolean isShowAddItem(int position) {
		int size = mDataList == null ? 0 : mDataList.size();
		return position == size;
	}

//	private void LoadImage(ImageView img, String path) {
//		// 异步加载图片资源
//		AsyncImageLoader async = new AsyncImageLoader(img);
//		// 执行异步加载，并把图片的路径传送过去
//		async.execute(path);
//
//	}
}
