package com.tower.countmanager.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.multipart.MultipartEntity;
import com.lidroid.xutils.http.client.multipart.content.FileBody;
import com.lidroid.xutils.http.client.multipart.content.StringBody;
import com.tower.countmanager.bean.ImageItem;
import com.tower.countmanager.cnst.Const;

import org.apache.http.entity.StringEntity;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public class RequestServerUtils {

	private static final int TIME_OUT = 15000;

	/**
	 * 使用JSON格式上传/请求数据
	 * @param request 请求表单JSON
	 * @param url 调用接口
	 * @param handler 回调对象
	 */
	public void load2Server(Map<String, Object> request, String url, final Handler handler) {
		
		try {
			RequestParams params = new RequestParams();
			//转换成JSON格式
	    	Gson gson = new Gson();
	    	String jsonStr = gson.toJson(request);
			StringEntity s = new StringEntity(jsonStr, "UTF-8");
			s.setContentType("application/json");
			//添加请求数据
			params.setBodyEntity(s);
		
			HttpUtils http = new HttpUtils();
			http.configTimeout(TIME_OUT);
			http.send(HttpRequest.HttpMethod.POST,
				url,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {}

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {}

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                    	Message msg = new Message();
                    	msg.what = Const.REQUEST_SERVER_SUCCESS;
                    	msg.obj = responseInfo.result;
                    	handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                    	Message m = new Message();
                    	m.what = Const.REQUEST_SERVER_FAILURE;
                    	m.obj = msg;
                    	handler.sendMessage(m);
                    }
                });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * 使用JSON格式上传/请求数据，带token
     * @param request 请求表单JSON
     * @param url 调用接口
     * @param handler 回调对象
     * @param token 请求token
     */
    public void load2Server(Map<String, Object> request, String url, final Handler handler, String token) {

        try {
            RequestParams params = new RequestParams();
            //转换成JSON格式
            Gson gson = new Gson();
            String jsonStr = gson.toJson(request);
            System.out.println("url------>"+url);
            System.out.println("jsonStr------>"+jsonStr);
            StringEntity s = new StringEntity(jsonStr, "UTF-8");
            s.setContentType("application/json");
            //添加请求数据
            params.setBodyEntity(s);
            params.addHeader("token", token);

            HttpUtils http = new HttpUtils();
            http.configTimeout(TIME_OUT);
            http.send(HttpRequest.HttpMethod.POST,
                    url,
                    params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onStart() {}

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {}

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Message msg = new Message();
                            msg.what = Const.REQUEST_SERVER_SUCCESS;
                            msg.obj = responseInfo.result;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Message m = new Message();
                            m.what = Const.REQUEST_SERVER_FAILURE;
                            m.obj = msg;
                            handler.sendMessage(m);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("RequestServerUtils", "no photo: ", e);
        }
    }
	
	/**
	 * 上传带有图片信息的表单JSON数据
	 * @param request 请求表单JSON
	 * @param url 请求地址
	 * @param mDataList 图片path
	 * @param handler
	 */
	public void load2Server(List<String> request, String url, List<ImageItem> mDataList, final Handler handler, String token) {
		
		try {
	        RequestParams params = new RequestParams(); // 默认编码UTF-8
            MultipartEntity me = new MultipartEntity();
            me.addPart("prjId", new StringBody(request.get(0), Charset.forName("UTF-8")));
            me.addPart("uploadStage", new StringBody(request.get(1), Charset.forName("UTF-8")));


            if(mDataList != null && mDataList.size() > 0) {
                for (ImageItem imageItem : mDataList) {
                    me.addPart("fileType", new StringBody(request.get(2), Charset.forName("UTF-8")));
                    me.addPart("file", new FileBody(new File(imageItem.getDoc_path())));
                }
            }
            params.setBodyEntity(me);
            params.addHeader("token", token);

	        HttpUtils http = new HttpUtils();
	        http.send(HttpRequest.HttpMethod.POST,
	        		url,
	                params,
	                new RequestCallBack<String>() {
	
	                    @Override
	                    public void onStart() {}
	
	                    @Override
	                    public void onLoading(long total, long current, boolean isUploading) {}
	
	                    @Override
	                    public void onSuccess(ResponseInfo<String> responseInfo) {
	                    	Message msg = new Message();
	                    	msg.what = Const.REQUEST_SERVER_SUCCESS;
	                    	msg.obj = responseInfo.result;
	                    	handler.sendMessage(msg);
	                    }
	
	                    @Override
	                    public void onFailure(HttpException error, String msg) {
	                    	Message m = new Message();
	                    	m.what = Const.REQUEST_SERVER_FAILURE;
	                    	m.obj = msg;
	                    	handler.sendMessage(m);
	                    }
	                });
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * 上传语音文件
     * @param taskSno 流水号
     * @param url 请求地址
     * @param filePath 语音path
     * @param handler
     */
    public void load2Server(String taskSno, String len, String url, String filePath, final Handler handler, String token) {

        try {
            RequestParams params = new RequestParams(); // 默认编码UTF-8
            MultipartEntity me = new MultipartEntity();
            me.addPart("task_sno", new StringBody(taskSno, Charset.forName("UTF-8")));
            me.addPart("second_length", new StringBody(len, Charset.forName("UTF-8")));
            me.addPart("file", new FileBody(new File(filePath)));
            params.setBodyEntity(me);
            params.addHeader("token", token);

            HttpUtils http = new HttpUtils();
            http.send(HttpRequest.HttpMethod.POST,
                    url,
                    params,
                    new RequestCallBack<String>() {

                        @Override
                        public void onStart() {}

                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {}

                        @Override
                        public void onSuccess(ResponseInfo<String> responseInfo) {
                            Message msg = new Message();
                            msg.what = Const.REQUEST_SERVER_SUCCESS;
                            msg.obj = responseInfo.result;
                            handler.sendMessage(msg);
                        }

                        @Override
                        public void onFailure(HttpException error, String msg) {
                            Message m = new Message();
                            m.what = Const.REQUEST_SERVER_FAILURE;
                            m.obj = msg;
                            handler.sendMessage(m);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     * @param url 请求地址
     * @param filePath 保存path
     */
    public void downloadFile(String url, String filePath) {

        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(1);
        HttpHandler<File> handle = http.download(
                url,
                filePath,
                true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
                false, // 如果从请求返回信息中获取到文件名，下载完成后自动重命名。
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {}

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {}

                    @Override
                    public void onSuccess(ResponseInfo<File> responseInfo) {
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                    }
                });
    }
}
