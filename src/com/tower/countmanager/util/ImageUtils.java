package com.tower.countmanager.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tower.countmanager.R;
import com.tower.countmanager.bean.ImageItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;


/**
 * 处理图片功能
 */
public class ImageUtils {
	
    private static boolean fileChannelCopy(File s, File t) {
        boolean result = true;

        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {

            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道

        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                if(fi != null)
                    fi.close();
                if(in != null)
                    in.close();
                if(fo != null)
                    fo.close();
                if(out != null)
                    out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // 图片处理
    private static void revitionImage(File s,File t) throws IOException {
        FileOutputStream out = null;
        try{
            // 取得图片
            InputStream temp = new FileInputStream(s);
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 这个参数代表，不为bitmap分配内存空间，只记录一些该图片的信息（例如图片大小），说白了就是为了内存优化
            options.inJustDecodeBounds = true;
            // 通过创建图片的方式，取得options的内容（这里就是利用了java的地址传递来赋值）
            BitmapFactory.decodeStream(temp, null, options);
            // 关闭流
            temp.close();

            // 生成压缩的图片
            Bitmap bitmap = null;
            // 重新取得流，注意：这里一定要再次加载，不能二次使用之前的流！
            temp = new FileInputStream(s);
            // 这个参数表示 新生成的图片为原始图片的几分之一。
            options.inSampleSize = 2;
            // 这里之前设置为了true，所以要改为false，否则就创建不出图片
            options.inJustDecodeBounds = false;

            bitmap = BitmapFactory.decodeStream(temp, null, options);
            /**
             * 把图片旋转为正的方向
             */
            int degree = readPictureDegree(s.getAbsolutePath());
            bitmap = rotaingImageView(degree, bitmap);

            // 关闭流
            temp.close();
            out = new FileOutputStream(t);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){
                out.flush();
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if(out != null){
                out.close();
            }
        }

    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    private static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 对选定的图片进行保存和处理
     * @param context context
     * @param oldUrl filePath
     * @param type 1:头像，2:普通
     * @return
     */
    public static String imageProcess(Context context, String oldUrl, int type){

		String newFileName = "";
		File backup = null;
		boolean copyResult = true;
		try {
            File cache = null;
            if(2 == type)
			    cache = FileUtil.getPhotosDir(context);
            else if(1 == type)
                cache = FileUtil.getUserIconDir(context);
			File OriginalImage = new File(oldUrl);
			long length = OriginalImage.length();
			// String suffix = newUrl.substring(newUrl.lastIndexOf("."));

			newFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
			backup = new File(cache, "/" + newFileName);
			if (!backup.exists()) {
				backup.createNewFile();
			}
			// if (length > 500000) {
			if (length > 300000) {
				revitionImage(OriginalImage, backup);
				OriginalImage = backup;
				length = OriginalImage.length();
				while (length > 300000) {
					newFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
					backup = new File(cache, "/" + newFileName);
					if (!backup.exists()) {
						backup.createNewFile();
					}
					revitionImage(OriginalImage, backup);
					OriginalImage.delete();
					OriginalImage = backup;
					length = OriginalImage.length();
					System.out.println("--------------" + length);
				}
			} else {
				fileChannelCopy(OriginalImage, backup);
			}
		} catch (Exception e) {
			Log.e("ImageUtils", "图片处理错误.", e);
		}

		return backup.toString();
	}

    //获得图片真实路径
    public static String getImageUrl(Context context, Uri originalUri) {
        String result = "";
        ContentResolver resolver = context.getContentResolver();
//        Bitmap bm = null;
        try {
//            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
            String[] proj = {MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery(originalUri, proj, null, null, null);
            Cursor cursor = resolver.query(originalUri, proj, null, null, null);
//            Cursor cursor = new CursorLoader(this, originalUri, proj, null, null, null).loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            //最后根据索引值获取图片路径
            result = cursor.getString(column_index);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    	
    public static void getFontBitmap(Context context, String path) {
        FileOutputStream fos = null;
        try {
            String latitude = "";
            String longitude = "";
            String time = "";
            double[] location = new double[1];
            if(location == null || location.length < 1)
                Toast.makeText(context, R.string.app_location_error_text, Toast.LENGTH_SHORT).show();
            else {
                latitude = context.getString(R.string.app_location_latitude) + location[0];
                longitude = context.getString(R.string.app_location_longitude) + location[1];
            }
            time = context.getString(R.string.app_photo_time) + Utils.getTime2String();

            Bitmap photo = BitmapFactory.decodeFile(path);
            int width = photo.getWidth(), hight = photo.getHeight();
            System.out.println("宽"+width+"高"+hight);
            Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
            Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

            Paint photoPaint = new Paint(); //建立画笔
            photoPaint.setDither(true); //获取跟清晰的图像采样
            photoPaint.setFilterBitmap(true);//过滤一些

            Rect src = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
            Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
            canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
            textPaint.setTextSize(15.0f);//字体大小
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
            textPaint.setColor(Color.WHITE);//采用的颜色
            //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
            canvas.drawText(latitude, 0, hight - 80, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
            canvas.drawText(longitude, 10, hight - 50, textPaint);
            canvas.drawText(time, 10, hight - 20, textPaint);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            fos = new FileOutputStream(new File(path));
            if(icon.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }    
	
    public static void getFontBitmap(Context context, String path, String latitude, String longitude) {
    	if(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)){
    		Log.e("Utils", "坐标获取不正确.");
    		return;
    	}	
    	
        FileOutputStream fos = null;
        try {
            String time = "";
            latitude = context.getString(R.string.app_location_latitude) + latitude;
            longitude = context.getString(R.string.app_location_longitude) + longitude;
            time = context.getString(R.string.app_photo_time) + Utils.getTime2String();

            Bitmap photo = BitmapFactory.decodeFile(path);
            int width = photo.getWidth(), hight = photo.getHeight();
            System.out.println("宽"+width+"高"+hight);
            Bitmap icon = Bitmap.createBitmap(width, hight, Bitmap.Config.ARGB_8888); //建立一个空的BItMap
            Canvas canvas = new Canvas(icon);//初始化画布绘制的图像到icon上

            Paint photoPaint = new Paint(); //建立画笔
            photoPaint.setDither(true); //获取跟清晰的图像采样
            photoPaint.setFilterBitmap(true);//过滤一些

            Rect src = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
            Rect dst = new Rect(0, 0, width, hight);//创建一个指定的新矩形的坐标
            canvas.drawBitmap(photo, src, dst, photoPaint);//将photo 缩放或则扩大到 dst使用的填充区photoPaint

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);//设置画笔
            textPaint.setTextSize(15.0f);//字体大小
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);//采用默认的宽度
            textPaint.setColor(Color.WHITE);//采用的颜色
            //textPaint.setShadowLayer(3f, 1, 1,this.getResources().getColor(android.R.color.background_dark));//影音的设置
            canvas.drawText(latitude, 10, hight - 80, textPaint);//绘制上去字，开始未知x,y采用那只笔绘制
            canvas.drawText(longitude, 10, hight - 50, textPaint);
            canvas.drawText(time, 10, hight - 20, textPaint);

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            fos = new FileOutputStream(new File(path));
            if(icon.compress(Bitmap.CompressFormat.JPEG, 100, fos)){
                fos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //获取图片缩小的图片
    public static Bitmap scaleBitmap(String src) {
        //获取图片的高和宽
        BitmapFactory.Options options = new BitmapFactory.Options();
        //这一个设置使 BitmapFactory.decodeFile获得的图片是空的,但是会将图片信息写到options中
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, options);

        options.inSampleSize = 4;
        //设置可以获取数据
        options.inJustDecodeBounds = false;
        //获取图片
        return BitmapFactory.decodeFile(src, options);


    }
    
    /**
	 * 水印照片
	 * 
	 * @param context
	 */

	public static ImageItem watermarkBitmap(Context context, String[] info) {
		String path = info[0];
		String time = info[1];
		String lon = info[2];
		String lat = info[3];
		if (TextUtils.isEmpty(time)) {
			time = Utils.getTime2String();
		}
		if (TextUtils.isEmpty(lon) || TextUtils.isEmpty(lat)) {
			lon = "未知";
			lat = "未知";
		}
		String title1 = context.getString(R.string.app_location_longitude) + lon;
		String title2 = context.getString(R.string.app_location_latitude) + lat;
		String title3 = context.getString(R.string.app_photo_time) + time;
		ImageItem item = new ImageItem();
		item.setDoc_path(path);
		item.setLongitude(lon);
		item.setLatitude(lat);
		FileOutputStream fos = null;
		try {
			Bitmap src = BitmapFactory.decodeFile(path);
			int w = src.getWidth();
			int h = src.getHeight();
			// 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
			Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
			Canvas cv = new Canvas(newb);
			cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
			Paint paint = new Paint();
			BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bg_photo_water_mark);
			Bitmap watermark = bd.getBitmap();
			// 加入图片
			if (watermark != null) {
				int ww = watermark.getWidth();
				int wh = watermark.getHeight();
				// paint.setAlpha(50);
				// cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, paint);//
				// 在src的右下角画入水印
				cv.drawBitmap(watermark, w - ww, h - wh, paint);
			}
			// 加入文字
			String familyName = "宋体";
			Typeface font = Typeface.create(familyName, Typeface.BOLD);
			TextPaint textPaint = new TextPaint();
			textPaint.setColor(Color.WHITE);
			textPaint.setTypeface(font);
			textPaint.setTextSize(18F);
			// 这里是自动换行的
			// StaticLayout layout = new
			// StaticLayout(title,textPaint,w,Alignment.ALIGN_NORMAL,1.0F,0.0F,true);
			// layout.draw(cv);
			cv.drawText(title3, 10, h - 20, textPaint);
			cv.drawText(title2, 10, h - 50, textPaint);
			cv.drawText(title1, 10, h - 80, textPaint);
			cv.save(Canvas.ALL_SAVE_FLAG);// 保存
			cv.restore();// 存储

			fos = new FileOutputStream(new File(path));
			if (newb.compress(Bitmap.CompressFormat.JPEG, 100, fos)) {
				fos.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return item;
	}
}
