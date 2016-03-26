package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.adapter.ImagePublishAdapter;
import com.tower.countmanager.bean.ImageItem;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.FileUtil;
import com.tower.countmanager.util.ImageUtils;
import com.tower.countmanager.util.LocationUtil;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.SGridView;
import com.tower.countmanager.view.SelectPicPopupWindow;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoCreateBackActivity extends Activity {

    private static final String TAG = "TodoCreateBackActivity";

	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.custom_process_info_gridview)
	SGridView mGridView;
    @ViewInject(R.id.todo_create_desc)
    private EditText completeDesc;
    @ViewInject(R.id.auto_operation_location_address)
    private TextView addressView;
    @ViewInject(R.id.auto_operation_location_latitude)
    private TextView latitudeView;
    @ViewInject(R.id.auto_operation_location_longitude)
    private TextView longitudeView;
    @ViewInject(R.id.todo_create_address_remark)
    private EditText addressRemark;
	@ViewInject(R.id.bar2)
	ProgressBar bar;
    LocationUtil locationUtil = null;
	ImagePublishAdapter mAdapter;
	ProgressDialog progressDialog = null;
    //自定义的弹出框类
    SelectPicPopupWindow menuWindow = null;
	String path = "";
	String newPath = "";// 增加水印照片
    String taskSno = "";//任务流水号
    String feedBackSeq = "";//反馈序列号
    String lon = "";
    String lat = "";
    BDLocation bdLocation = null;
    private ProgressDialog dialog = null;
	// public List<ImageItem> mDataList = new ArrayList<ImageItem>();
	// 存储生成照片行每个item的所有控件
	private static final int REQUEST_ACTIVITY_REMARK_INFO = 0x00011;
	private static final int REQUEST_ACTIVITY_MAP = 0x00012;
	private static final int REQUEST_ACTIVITY_IMAGE_ZOOM = 0x00013;
	private static final int REQUEST_SELECT_IMAGE = 0x00014;

	private static final int ID_REQUEST_LOAD_PROCESS_INFO = 0x00021;
	private static final int ID_REQUEST_UPLOAD_PROCESS_TEXT_INFO = 0x00022;
	String processId;
	String projectId;
	String provinceId;
	String currentType = null;
	ArrayList<ImageItem> mDataList = new ArrayList<ImageItem>();

	// 读系统照片相关字段信息
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media._ID };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_ceate_back);
        ViewUtils.inject(this);

		init();
	}

    private void init() {
        subviewTitle.setText(R.string.back_info_feedback_title);
        taskSno = getIntent().getStringExtra("taskSno");
        initGridView();
        locationUtil = new LocationUtil(this);
        geLocation();

        initData();
    }

	private void initGridView() {
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		mAdapter = new ImagePublishAdapter(this, mDataList, true, Const.MAX_IMAGE_SIZE);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == getDataSize(mDataList)) {
                    menuWindow = new SelectPicPopupWindow(TodoCreateBackActivity.this, itemsOnClick);
                    //显示窗口
                    //设置layout在PopupWindow中显示的位置
                    menuWindow.showAtLocation(findViewById(R.id.todo_create_back_layout),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					List<String> pathList = new ArrayList<String>();
					for (ImageItem imageItem : mDataList) {
						pathList.add(imageItem.getDoc_path());
					}
					Intent intent = new Intent(TodoCreateBackActivity.this, ImageZoomActivity.class);
					intent.putStringArrayListExtra(Const.EXTRA_IMAGE_LIST, (ArrayList<String>) pathList);
					intent.putExtra(Const.EXTRA_CURRENT_IMG_POSITION, position);
					intent.putExtra(Const.IS_SHOW_DEL_BTN, true);
					startActivityForResult(intent, REQUEST_ACTIVITY_IMAGE_ZOOM);
				}
			}
		});
	}

    @OnClick(value = {R.id.subview_title_arrow, R.id.voice_message, R.id.layout_refresh, R.id.todo_create_commit_button})
    public void onButtonClick(View v){

        switch(v.getId()) {
            case R.id.subview_title_arrow :
                finish();
                break;
            case R.id.voice_message :
                startActivity(new Intent(this, VoiceActivity.class).putExtra("task_sno", taskSno));
                break;
            case R.id.layout_refresh :
            	addressView.setText("");
            	latitudeView.setText("");
            	longitudeView.setText("");
            	bar.setVisibility(View.VISIBLE);
                locationUtil.getLocation(new LocationUtil.LocationCallBack() {
                    @Override
                    public void onSuccess(BDLocation location) {
                        bdLocation = location;
                        Log.e("===========", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude() + ", address: " + location.getAddrStr());
                        addressView.setText(location.getAddrStr());
                        latitudeView.setText(String.format(getString(R.string.auto_operation_location_latitude), location.getLatitude()));
                        longitudeView.setText(String.format(getString(R.string.auto_operation_location_longitude), location.getLongitude()));
                        lon = location.getLongitude() + "";
                        lat = location.getLatitude() + "";
                    	bar.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.todo_create_commit_button :
                if (!Utils.isConnect(this)) {
                    Toast.makeText(this, R.string.app_connecting_network_no_connect, Toast.LENGTH_SHORT).show();
                } else {
                    commitPhoto();
                }
                break;
        }
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener(){

        public void onClick(View v) {
            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_popupwindows_camera:
                    takePhoto();
                    break;
                case R.id.item_popupwindows_Photo:
                    selectPicFromLocal();
                    break;
                default:
                    break;
            }
        }
    };

    private void geLocation() {

        locationUtil.getLocation(new LocationUtil.LocationCallBack() {
            @Override
            public void onSuccess(BDLocation location) {
                bdLocation = location;
                Log.e("===========", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude() + ", address: " + location.getAddrStr());
                addressView.setText(location.getAddrStr());
                latitudeView.setText(String.format(getString(R.string.auto_operation_location_latitude), location.getLatitude()));
                longitudeView.setText(String.format(getString(R.string.auto_operation_location_longitude), location.getLongitude()));
                lon = location.getLongitude() + "";
                lat = location.getLatitude() + "";
            }
        });
    }

	private int getDataSize(List<ImageItem> mDataList) {
		return mDataList == null ? 0 : mDataList.size();
	}

	/**
	 * 图片相应处理
	 * 
	 * @param info
	 */
	private void dealImage(String[] info) {
		if (mDataList.size() < Const.MAX_IMAGE_SIZE && !TextUtils.isEmpty(info[0])) {
			String oldPath = info[0];
			newPath = ImageUtils.imageProcess(TodoCreateBackActivity.this, oldPath, 2);
			info[0] = newPath;
			mDataList.add(ImageUtils.watermarkBitmap(TodoCreateBackActivity.this, info));
		}
		notifyDataChanged(mAdapter);
	}

	/**
	 * 更新数据
	 * 
	 * @param mAdapter
	 */
	private void notifyDataChanged(ImagePublishAdapter mAdapter) {
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 从图库获取图片，sdk版本不同方式有点区分
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 17) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}
		startActivityForResult(intent, REQUEST_SELECT_IMAGE);
	}

	/**
	 * 拍照
	 */
	public void takePhoto() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File vFile = new File(FileUtil.getPhotosDir(this), String.valueOf(System.currentTimeMillis()) + ".jpg");

		if (!vFile.exists()) {
			File vDirPath = vFile.getParentFile();
			vDirPath.mkdirs();

		} else {
			if (vFile.exists()) {
				vFile.delete();
			}
		}
		path = vFile.getPath();
		Uri cameraUri = Uri.fromFile(vFile);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
		startActivityForResult(openCameraIntent, Const.TAKE_PICTURE);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case Const.TAKE_PICTURE:

                if(TextUtils.isEmpty(lon)) {
                    locationUtil.getLocation(new LocationUtil.LocationCallBack() {
                        @Override
                        public void onSuccess(BDLocation location) {
                            lon = location.getLongitude() + "";
                            lat = location.getLatitude() + "";
                        }
                    });
                }
				String time = Utils.getTime2String();
				String[] info = { path, time, lon, lat };
				if (path != null && path.length() > 0) {
					dealImage(info);
				}
				// 水印照片生成后，删除原图
//				FileUtil.deleteFile(path);
				break;
			case REQUEST_ACTIVITY_IMAGE_ZOOM:
				if (data != null) {
					List<String> delPositions = data.getStringArrayListExtra("delPositions");
					Collections.sort(delPositions);
					for (int i = mDataList.size() - 1; i >= 0; i--) {
						for (String delIndex : delPositions) {
							int index = Integer.parseInt(delIndex);
							if (i == index) {
								mDataList.remove(i);
							}
						}
					}
					notifyDataChanged(mAdapter);
				}
				break;
			case REQUEST_SELECT_IMAGE:
				if (data != null) {
					Uri uri = data.getData();
					// if (!Utils.isImage(Utils.getFileType(uri.toString()))) {
					// Toast.makeText(this, "请选择图片文件",
					// Toast.LENGTH_SHORT).show();
					// return;
					// }
					// 读取相册照片相应字段信息
					CursorLoader cursorLoader = new CursorLoader(this, uri, STORE_IMAGES, null, null, null);
					Cursor cursor = cursorLoader.loadInBackground();
					cursor.moveToFirst();
					// 照片真实路径
					String path = cursor.getString(cursor.getColumnIndex(STORE_IMAGES[0]));
					// 照片生成的日期
					long date = cursor.getLong(cursor.getColumnIndex(STORE_IMAGES[1]));
					// 格式化时间
					String dateStr = Utils.getFormatDate(date, "yyyy/MM/dd HH:mm");
					// 经度
					String longitude = cursor.getString(cursor.getColumnIndex(STORE_IMAGES[2]));
					// 纬度
					String latitude = cursor.getString(cursor.getColumnIndex(STORE_IMAGES[3]));
					String[] albutInfo = { path, dateStr, longitude, latitude };
					if (path != null && path.length() > 0) {
						dealImage(albutInfo);
					}
				}
				break;
			default:
				break;
			}
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
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.TODO_CREATE_CALLBACK_INFO_URL, handler, Utils.getToken(this));
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
                            Toast.makeText(TodoCreateBackActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){
                            feedBackSeq = new JSONObject(jsonObject.getString("result")).getString("feedBackSeq");
                            Log.e("==========", feedBackSeq);
                        } else
                            Utils.sessionTimeout(TodoCreateBackActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoCreateBackActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void commitPhoto() {
        createDialog();
        List<String> data = new ArrayList<String>();
        data.add(feedBackSeq);
        data.add("P002");
        data.add("GZFK");
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(data, Const.UPLOAD_PHOTO_URL, mDataList, photoHandler, Utils.getToken(this));
    }

    private Handler photoHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            Log.e(TAG, msg.obj.toString());
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String success = jsonObject.getString("success");
                        if("02".equals(success)) {
                            Utils.sessionTimeout(TodoCreateBackActivity.this);
                        } else if("01".equals(success)){
                            commitButton();
                        } else if("00".equals(success)) {
                            if (dialog != null)
                                dialog.dismiss();
                            Toast.makeText(TodoCreateBackActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoCreateBackActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    if (dialog != null)
                        dialog.dismiss();
                    break;
            }
        }
    };

    private void commitButton() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("feedback_sno", feedBackSeq);
        map.put("task_sno", taskSno);
        map.put("feedback_longitude", bdLocation.getLatitude());
        map.put("feedback_latitude", bdLocation.getLongitude());
        map.put("feedback_address", bdLocation.getAddrStr());
        map.put("feedback_address_remark", addressRemark.getText().toString());
        map.put("feedback_complete_desc", completeDesc.getText().toString());
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.TODO_CREATE_CALLBACK_URL, commitHandler, Utils.getToken(this));
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
                            Toast.makeText(TodoCreateBackActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){
                            Toast.makeText(TodoCreateBackActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                            setResult(90);
                            finish();
                        } else
                            Utils.sessionTimeout(TodoCreateBackActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(TodoCreateBackActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}
