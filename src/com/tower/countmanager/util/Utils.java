package com.tower.countmanager.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tower.countmanager.cnst.Const;

public class Utils {

	private static final char[] legalChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
			.toCharArray();
	
	/**
	 * 判断网络是否打开
	 * @return
	 */
	public static boolean isConnect(Context c) {
		boolean isConnFlag = false;		
        ConnectivityManager conManager = 
        		(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(conManager != null) {
        	NetworkInfo network = conManager.getActiveNetworkInfo();
        	if(network != null){
        		isConnFlag = network.isAvailable();
        	}    
        }
        return isConnFlag;
	}

    /**
	 * 获取屏幕宽度
	 * @return
	 */
	public static int getScreenWidth(Context context) {

		DisplayMetrics localDisplayMetrics = new DisplayMetrics();
		WindowManager mWindowManager = 
				(WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.getDefaultDisplay().getMetrics(localDisplayMetrics);
		int screenW = localDisplayMetrics.widthPixels;
		return screenW;
	}
	
    /**
     * 获取当前时间，格式：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getTime2String() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取当前时间，格式：yyyy-MM-dd
     * @return
     */
    public static String getData2String() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取当前时间，格式：yyyy-MM
     * @return
     */
    public static String getMonth2String() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(new Date(System.currentTimeMillis()));
    }
    
    /**
     * 获取上个月，格式：yyyy-MM
     * @return
     */
	public static String getLastMonth() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
		String time = format.format(c.getTime());
		return time;
	}

    
    /**
     * 根据指定格式格式化时间字符串
     *
     * @param time
     * @param format
     * @return
     */
    public static String getFormatDate(long time, String format) {
        String dateStr = "";
        try {
            SimpleDateFormat format1 = new SimpleDateFormat(format, Locale.getDefault());
            Date d1 = new Date(time);
            dateStr = format1.format(d1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    /**
     * 获取用户ID
     * @return
     */
	public static String getUserId(Context ctx){
		String result = ctx.getSharedPreferences(Const.SHARED_PREF,
				Activity.MODE_PRIVATE).getString(Const.USER_ID, "");
		return result;
	}

    /**
     * 获取token
     * @return
     */
    public static String getToken(Context ctx){
        String result = ctx.getSharedPreferences(Const.SHARED_PREF,
                Activity.MODE_PRIVATE).getString(Const.TOKEN, "");
        return result;
    }

    /**
     * 获取roleId
     * @return
     */
    public static String getRoleId(Context ctx){
        String result = ctx.getSharedPreferences(Const.SHARED_PREF,
                Activity.MODE_PRIVATE).getString(Const.ROLE_ID, "");
        return result;
    }

    /**
     * 获取userPhoto
     * @return
     */
    public static String getUserPhoto(Context ctx){
        String result = ctx.getSharedPreferences(Const.SHARED_PREF,
                Activity.MODE_PRIVATE).getString(Const.USER_PHOTO, "");
        return result;
    }

    /**
     * 服务端返回99，timeout处理
     */
    public static void sessionTimeout(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    //删除myImage文件夹中的图片
    public static void deletePicFromMyImage(Context context) {
        String path = Environment.getExternalStorageDirectory() + "/countyManager/Android/data/" + context.getPackageName() + "/Photos";
        deleteFolderFiles(path);
    }

	/**
	 * 删除文件夹内部所有文件
	 * 
	 * @param path
	 * @return
	 */
	public static void deleteFolderFiles(String path) {
		File file = new File(path);
		if (!file.exists())
			return;

		if (!file.isDirectory())
			return;

		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator))
				temp = new File(path + tempList[i]);
			else
				temp = new File(path + File.separator + tempList[i]);

			if (temp.isFile())
				temp.delete();

			if (temp.isDirectory()) {
				deleteFolderFiles(path + "/" + tempList[i]);// 先删除文件夹里面的文件
			}
		}
	}

	/**
	 * 获取应用版本号
	 * 
	 * @return 版本号
	 */
	public static int getVersionCode(Context context) {
		PackageInfo packInfo;
		int version;
		try {
			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}

		return version;
	}

}
