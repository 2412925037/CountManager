package com.tower.countmanager.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtil {
	public static File updateDir = null;
	public static File updateFile = null;
	/**
	 * sd卡的根目录
	 */
	private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();

	/**
	 * 获取照片存储目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getPhotosDir(Context context) {
		final String local_file = "/Android/data/"
				+ context.getPackageName() + "/Photos";
		File f = new File(mSdRootPath + local_file);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

    /**
     * 获取头像照片存储目录
     *
     * @param context
     * @return
     */
    public static File getUserIconDir(Context context) {
        final String local_file = "/Android/data/"
                + context.getPackageName() + "/Photos/" + Utils.getUserId(context);
        File f = new File(mSdRootPath + local_file);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    /**
     * 设置语音文件路径
     */
    public static String getVoiceFilePath(Context context) {
        return Environment.getExternalStorageDirectory()+ "/countyManager/Voice/" + Utils.getUserId(context);
    }

	/**
	 * 删除指定目录及文件
	 */
	public static void deleteFiles(File dirFile) {
		// File dirFile = getImageCacheDir(context);
		if (!dirFile.exists()) {
			return;
		}
		if (dirFile.isDirectory()) {
			String[] children = dirFile.list();
			for (int i = 0; i < children.length; i++) {
				new File(dirFile, children[i]).delete();
			}
		}
		dirFile.delete();
	}
	
	/**
	 * 删除指定目录及文件
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		deleteFiles(file);
	}
	

	/**
	 * 判断文件是否存在
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

}
