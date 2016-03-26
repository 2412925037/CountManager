package com.tower.countmanager.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncImageLoader extends AsyncTask<String, Integer, SoftReference<Bitmap>> {
	private ImageView Image = null;
	public AsyncImageLoader(ImageView img) {
    	Image = img;
    }
	
	//运行在子线程中
    protected SoftReference<Bitmap> doInBackground(String... params) {

        try {
			URL thumb_u = new URL(params[0]);
			InputStream in = thumb_u.openStream();
			BitmapFactory.Options opts = new BitmapFactory.Options(); 
		    opts.inSampleSize = 2;
		    Bitmap thumb_d = BitmapFactory.decodeStream(in, null, opts); 
            //Drawable thumb_d = Drawable.createFromStream(thumb_u.openStream(), "src");
            return new SoftReference<Bitmap>(thumb_d);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}           
		return null;
    }
	
	/**
	 * 回调接口
	 * @author onerain
	 *
	 */
    protected void onPostExecute(SoftReference<Bitmap> result){
    	
        if(Image!=null && result!=null){
            Image.setImageBitmap(result.get());
        }
        super.onPostExecute(result);
    }
}
