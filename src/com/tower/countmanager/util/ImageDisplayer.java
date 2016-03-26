package com.tower.countmanager.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.tower.countmanager.R;


public class ImageDisplayer {
	private static ImageDisplayer instance;
	private Context context;
	private static final int THUMB_WIDTH = 256;
	private static final int THUMB_HEIGHT = 256;
	private int mScreenWidth;
	private int mScreenHeight;

	public static ImageDisplayer getInstance(Context context) {
		if (instance == null) {
			synchronized (ImageDisplayer.class) {
				instance = new ImageDisplayer(context);
			}
		}

		return instance;
	}

	public ImageDisplayer(Context context) {
		if (context.getApplicationContext() != null)
			this.context = context.getApplicationContext();
		else
			this.context = context;

		DisplayMetrics dm = new DisplayMetrics();
		dm = this.context.getResources().getDisplayMetrics();
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
	}

	public Handler h = new Handler();
	public final String TAG = getClass().getSimpleName();
	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public void put(String key, Bitmap bmp) {
		if (!TextUtils.isEmpty(key) && bmp != null) {
			imageCache.put(key, new SoftReference<Bitmap>(bmp));
		}
	}

	public void displayBmp(final ImageView iv, final String thumbPath,
			final String sourcePath) {
		displayBmp(iv, thumbPath, sourcePath, false);
	}

	public void displayBmp(final ImageView iv, final String thumbPath,
			final String sourcePath, final boolean showThumb) {
		if (TextUtils.isEmpty(thumbPath) && TextUtils.isEmpty(sourcePath)) {
			Log.e(TAG, "no paths pass in");
			return;
		}

		if (iv.getTag() != null && iv.getTag().equals(sourcePath)) {
			return;
		}

		showDefault(iv);

		final String path;
		if (!TextUtils.isEmpty(thumbPath) && showThumb) {
			path = thumbPath;
		} else if (!TextUtils.isEmpty(sourcePath)) {
			path = sourcePath;
		} else {
			return;
		}

		iv.setTag(path);

		if (imageCache.containsKey(showThumb ? path + THUMB_WIDTH
				+ THUMB_HEIGHT : path)) {
			SoftReference<Bitmap> reference = imageCache.get(showThumb ? path
					+ THUMB_WIDTH + THUMB_HEIGHT : path);
			// 可以用LruCahche会好些
			Bitmap imgInCache = reference.get();
			if (imgInCache != null) {
				refreshView(iv, imgInCache, path);
				return;
			}else {
				
			}
		}
		iv.setImageBitmap(null);

		// 不在缓存则加载图片
		new Thread() {
			Bitmap img;

			public void run() {

				try {
					if (path != null && path.equals(thumbPath)) {
						img = BitmapFactory.decodeFile(path);
					}
					if (img == null) {
						img = compressImg(sourcePath, showThumb);

					}
				} catch (Exception e) {

				}

				if (img != null) {
					put(showThumb ? path + THUMB_WIDTH + THUMB_HEIGHT : path,
							img);

				}
				h.post(new Runnable() {
					@Override
					public void run() {
						refreshView(iv, img, path);
					}
				});
			}
		}.start();

	}

	private void refreshView(ImageView imageView, Bitmap bitmap, String path) {
		if (imageView != null && bitmap != null) {
			if (path != null) {
				((ImageView) imageView).setImageBitmap(bitmap);
				imageView.setTag(path);
			}
		}
	}

	private void showDefault(ImageView iv) {
		iv.setBackgroundResource(R.drawable.icon_add_photo);
	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		;
		matrix.postRotate(angle);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	public Bitmap compressImg(String path, boolean showThumb)
			throws IOException {
		int degree = readPictureDegree(path);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, opt);
		in.close();
		int i = 1;
		Bitmap bitmap = null;

		if (showThumb) {
			while (true) {
				if ((opt.outWidth >> i <= THUMB_WIDTH)
						&& (opt.outHeight >> i <= THUMB_HEIGHT)) {
					in = new BufferedInputStream(new FileInputStream(new File(
							path)));
					opt.inSampleSize = (int) Math.pow(2.0D, i);
					opt.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, opt);
					break;
				}
				i += 1;
			}
		} else {
			while (true) {
				if ((opt.outWidth >> i <= mScreenWidth)
						&& (opt.outHeight >> i <= mScreenHeight)) {
					in = new BufferedInputStream(new FileInputStream(new File(
							path)));
					opt.inSampleSize = 4;
					opt.inJustDecodeBounds = false;
					bitmap = BitmapFactory.decodeStream(in, null, opt);
					break;
				}
				i += 1;
			}
		}

		/**
		 * 把图片旋转为正的方向
		 */
		bitmap = rotaingImageView(degree, bitmap);
		return bitmap;
	}

	public interface ImageCallback {
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params);
	}
}
