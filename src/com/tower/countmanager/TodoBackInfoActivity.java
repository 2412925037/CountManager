package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.BackInfoBean;
import com.tower.countmanager.bean.ImageBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoBackInfoActivity extends Activity {

    private static final String TAG = "TodoCreateBackActivity";

	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.back_info_gridview)
    GridView mGridView;
    @ViewInject(R.id.back_info_desc)
    private TextView descView;
    @ViewInject(R.id.auto_operation_location_latitude)
    private TextView latitudeView;
    @ViewInject(R.id.auto_operation_location_longitude)
    private TextView longitudeView;
    @ViewInject(R.id.back_info_address)
    private TextView addressView;
    @ViewInject(R.id.back_info_remark)
    private TextView addressRemarkView;
    @ViewInject(R.id.back_info_opinion)
    private EditText opinionView;
    @ViewInject(R.id.back_info_opinion_layout)
    private LinearLayout opinionLayout;
    @ViewInject(R.id.layout_button_commit)
    private RelativeLayout btnLayout;
    

    String feedBackSno = "";//反馈序列号
    String taskSno = "";
    private ProgressDialog dialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_back_info);
        ViewUtils.inject(this);

		init();
	}

    private void init() {
        subviewTitle.setText(R.string.back_info_feedback_title);
        feedBackSno = getIntent().getStringExtra("feedBackSno");

//        if(Utils.getRoleId(this).equals("2"))
//            opinionLayout.setVisibility(View.GONE);
        initData();
    }


    @OnClick(value = {R.id.subview_title_arrow, R.id.voice_message, R.id.back_info_commit_button})
    public void onButtonClick(View v){

        switch(v.getId()) {
            case R.id.subview_title_arrow :
                finish();
                break;
            case R.id.voice_message :
                startActivity(new Intent(this, VoiceActivity.class).putExtra("task_sno", taskSno));
                break;
            case R.id.back_info_commit_button :
                if (!Utils.isConnect(this)) {
                    Toast.makeText(this, R.string.app_connecting_network_no_connect, Toast.LENGTH_SHORT).show();
                } else {
                    commitButton();
                }
                break;
        }
    }

    private void createDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

    private void initData() {
        createDialog();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("feedback_sno", feedBackSno);
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.TODO_FEEDBACK_INFO_URL, handler, Utils.getToken(this));
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (dialog != null)
                dialog.dismiss();
            Log.e(TAG, (String) msg.obj);
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject((String) msg.obj);
                        String success = jsonObject.getString("success");
                        if ("00".equals(success)) {
                            Toast.makeText(TodoBackInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){

                            Gson gson = new Gson();
                            BackInfoBean bean = gson.fromJson(jsonObject.getString("result"), BackInfoBean.class);
                            descView.setText(bean.getComplete_desc());
                            latitudeView.setText(String.format(getString(R.string.auto_operation_location_latitude), bean.getLatitude()));
                            longitudeView.setText(String.format(getString(R.string.auto_operation_location_longitude), bean.getLongitude()));
                            addressView.setText(bean.getAddress());
                            addressRemarkView.setText(bean.getAddress_remark());
                            final List<ImageBean> imageBeanList = bean.getImgList();
                            if(imageBeanList != null && imageBeanList.size() > 0) {
                            	mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
                                mGridView.setAdapter(new ImageAdapter(imageBeanList));
                                mGridView.setOnItemClickListener(new OnItemClickListener() {
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									List<String> pathList = new ArrayList<String>();
									for (ImageBean imageItem : imageBeanList) {
										pathList.add(imageItem.getDocPath());
									}
									Intent intent = new Intent(TodoBackInfoActivity.this, ImageZoomActivity.class);
									intent.putStringArrayListExtra(Const.EXTRA_IMAGE_LIST, (ArrayList<String>) pathList);
									intent.putExtra(Const.EXTRA_CURRENT_IMG_POSITION, position);
									intent.putExtra(Const.IS_SHOW_DEL_BTN, false);
									startActivity(intent);
								}
                        		});
                            }
                            taskSno = bean.getTask_sno();
                            //区域经理，有批复意见不可编辑，无意见时可编辑;县域经理只可查看区域经理给的反馈意见
                            if (("2".equals(Utils.getRoleId(TodoBackInfoActivity.this)) || (!TextUtils.isEmpty(bean.getRe_feedback_info())))) {
                            	opinionView.setText(bean.getRe_feedback_info());
                            	opinionView.setHint(null);
                            	opinionView.setEnabled(false);
                            	btnLayout.setVisibility(View.GONE);
							}
                            
                        } else
                            Utils.sessionTimeout(TodoBackInfoActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoBackInfoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void commitButton() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("feedBackSeq", feedBackSno);
        map.put("feedBackOpinion", opinionView.getText().toString());
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.TODO_FEEDBACK_COMMIT_URL, commitHandler, Utils.getToken(this));
    }

    private Handler commitHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if (dialog != null)
                dialog.dismiss();
            Log.e(TAG, (String) msg.obj);
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject((String) msg.obj);
                        String success = jsonObject.getString("success");
                        if ("00".equals(success)) {
                            Toast.makeText(TodoBackInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){
                            Toast.makeText(TodoBackInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else
                            Utils.sessionTimeout(TodoBackInfoActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoBackInfoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    class ImageAdapter extends BaseAdapter {

        List<ImageBean> photoList;
        public ImageAdapter(List<ImageBean> photoList) {
            this.photoList = photoList;
        }

        @Override
        public int getCount()
        {
            return photoList == null ? 0 : photoList.size();
        }

        @Override
        public Object getItem(int position)
        {
            return photoList.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ViewHolder mHolder = null;
            if (convertView == null){
                mHolder = new ViewHolder();
                convertView = View.inflate(TodoBackInfoActivity.this, R.layout.item_publish, null);
                mHolder.imageIv = (ImageView) convertView.findViewById(R.id.item_grid_image);
                convertView.setTag(mHolder);
            }
            else
                mHolder = (ViewHolder) convertView.getTag();

            String url = photoList.get(position).getDocPath();
//            Bitmap cachedImage = asyncImageLoader.loadDrawable(mHolder.imageIv, url, new AsyncImageLoaderUtil.ImageCallback() {
//
//                public void imageLoaded(Object obj, Bitmap imageDrawable, String imageUrl) {
//
//                    ImageView iv = (ImageView)obj;
//                    if(iv != null)
//                        iv.setImageBitmap(imageDrawable);
//                }
//            });
//
//            if(cachedImage != null)
//                mHolder.imageIv.setImageBitmap(cachedImage);

            AsyncImageLoader loader = new AsyncImageLoader(mHolder.imageIv);
            loader.execute(url);
            return convertView;
        }
    }

    class ViewHolder {
        private ImageView imageIv;
    }

}
