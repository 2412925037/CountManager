package com.tower.countmanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
/**
 * 汇报任务
 */
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class DraftReportTaskDetail2Activity extends Activity {

    private static final String TAG = "DraftReportTaskDetailActivity";
    private Context mAppContext = null;
	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;

	@ViewInject(R.id.custom_process_info_gridview)
	SGridView mGridView;
	@ViewInject(R.id.btn_save)
	private Button btnSave;
	ImagePublishAdapter mAdapter;
	ProgressDialog progressDialog = null;
	@ViewInject(R.id.task_num)
	TextView taskNum;
	String path = "";
	String newPath = "";// 增加水印照片
    String taskSno = "";//任务流水号
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
	@ViewInject(R.id.task_recieve_person)
	TextView taskPerson;
	@ViewInject(R.id.task_type)
	TextView taskType;
	@ViewInject(R.id.task_name)
	TextView task_name;
	@ViewInject(R.id.task_type2)
	EditText task_type2;
	@ViewInject(R.id.edit_msg)
	EditText edit_msg;
	@ViewInject(R.id.task_info_information)
	EditText task_info_information;
	@ViewInject(R.id.auto_operation_remark)
	EditText auto_operation_remark;
	@ViewInject(R.id.task_name)
	EditText taskName;
	@ViewInject(R.id.site_info_name)
	TextView siteInfoName;
	@ViewInject(R.id.site_info_location)
	TextView siteInfoLocation;
	@ViewInject(R.id.site_info_addr)
	TextView siteInfoAddr;
	@ViewInject(R.id.tv_place)
	TextView tv_place;	
	@ViewInject(R.id.tv_while)
	TextView tv_while;
	@ViewInject(R.id.task_info_person)
	ImageView taskinfoImage;
	@ViewInject(R.id.img_task_info_next)
	ImageView tasknextImage;
	
	private String[] taskTypes;
	private String[] taskRecievePersons;
	private String[] taskRecievePersonValues;
	private String[] emergencyDegree;
	private String[] multipleFeedback;
	int whichTaskType = 0;
	int whichTaskRecievePerson = 0;
	private String type="";
	private String taskTypeNum="";//任务类型编码
	private String user_id="";
	String longitudes;
	String latitudes;
	private String task_sno="";
	private String site_sno="";
	@ViewInject(R.id.button_report)
	RelativeLayout btnLayout;
	// 读系统照片相关字段信息
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media._ID };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_task2);
		ViewUtils.inject(this);
		 mAppContext = this.getApplicationContext();
		initViews();
		initGridView();
		getMsg();
		init();
	}
	
	private void init() {
		// TODO Auto-generated method stub
		task_type2.setFocusable(false);
		edit_msg.setFocusable(false);
		task_info_information.setFocusable(false);
		auto_operation_remark.setFocusable(false);
		taskName.setFocusable(false);
		btnLayout.setVisibility(View.INVISIBLE);
		taskinfoImage.setVisibility(View.INVISIBLE);
		tasknextImage.setVisibility(View.INVISIBLE);
	}
	
	@OnClick(value = { R.id.subview_title_arrow})
	public void onButtonClick(View v) {
		switch (v.getId()) {
		case R.id.subview_title_arrow:
			finish();
			break;
		}
	}

	private void initViews(){
		emergencyDegree = getResources().getStringArray(R.array.task_emergency_degree);
		multipleFeedback = getResources().getStringArray(R.array.task_multiple_feedback);
		subviewTitle.setText(R.string.report_task_title);
		titleLeftImg.setVisibility(View.VISIBLE);
		taskTypes = getResources().getStringArray(R.array.task_type);
		loadTaskRecievePerson();
		loadTaskInfo();
	}

	private void initGridView() {
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		mAdapter = new ImagePublishAdapter(this, mDataList, true, Const.MAX_IMAGE_SIZE);
		mGridView.setAdapter(mAdapter);
		
	}

    
	public void getMsg(){
		RequestServerUtils util = new RequestServerUtils();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("task_sno",getIntent().getStringExtra("task_sno"));
		map.put("workItemId", "");
		util.load2Server(map, Const.GET_TASK_REPORT_INFO_URL, handlers, Utils.getToken(this));
	}
	
	private Handler handlers = new Handler() {
		public void handleMessage(android.os.Message msg) {
			  Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(DraftReportTaskDetail2Activity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							site_sno=jsonObject2.getString("site_sno");
							task_sno=jsonObject2.getString("task_sno");
							taskTypeNum= jsonObject2.getString("task_type_code");
							if("T1".equals(taskTypeNum)){
								taskType.setText("新建基站");
							}else if("T2".equals(taskTypeNum)){
								taskType.setText("存量改造");
							}else if("T3".equals(taskTypeNum)){
								taskType.setText("项目管理");
							}else if("T4".equals(taskTypeNum)){
								taskType.setText("其他");
							}
							
							task_type2.setText(jsonObject2.getString("task_type2"));
							task_name.setText(jsonObject2.getString("task_name"));
							task_info_information.setText(jsonObject2.getString("task_info"));
			                siteInfoName.setText(jsonObject2.getString("site_name"));
			                siteInfoLocation.setText("经度:"+jsonObject2.getString("longitude")+"  纬度:"+jsonObject2.getString("latitude"));
			                siteInfoAddr.setText(jsonObject2.getString("province")+"省、"+jsonObject2.getString("city")+"市、"+jsonObject2.getString("district"));
			                taskPerson.setText(jsonObject2.getString("emp_names"));
			                auto_operation_remark.setText(jsonObject2.getString("feedback_address_remark"));
			                edit_msg.setText(jsonObject2.getString("feedback_complete_desc"));
			                tv_place.setText("Latitude: " + jsonObject2.getString("feedback_latitude") + ",Longitude: " + jsonObject2.getString("feedback_longitude"));
			                tv_while.setText(jsonObject2.getString("feedback_address"));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(DraftReportTaskDetail2Activity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/**
	 * 拉取任务接收人
	 */
	private void loadTaskRecievePerson() {
		RequestServerUtils util = new RequestServerUtils();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("role_id", "1");
		map.put("start", "0");
		map.put("limit", "100");
		util.load2Server(map, Const.GET_TASK_RECIEVE_PERSON_URL, handlerLoadRecievePerson, Utils.getToken(this));
	}
	private Handler handlerLoadRecievePerson = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(DraftReportTaskDetail2Activity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						JSONArray jsonArray = jsonObject2.getJSONArray("rows");
						taskRecievePersons = new String[jsonArray.length()];
						taskRecievePersonValues = new String[jsonArray.length()];
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jobj = jsonArray.getJSONObject(i);
							taskRecievePersons[i] = jobj.getString("text");
							taskRecievePersonValues[i] = jobj.getString("value");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(DraftReportTaskDetail2Activity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/**
	 * 拉取任务信息
	 */
	private void loadTaskInfo() {
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(null, Const.GET_TASK_CODE_URL, handlerLoad, Utils.getToken(this));
	}
	private Handler handlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(DraftReportTaskDetail2Activity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							String task_code = jsonObject2.getString("task_code");
							taskNum.setText(task_code);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(DraftReportTaskDetail2Activity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
    private void temporarySave(String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        taskTypes();
        map.put("task_sno",task_sno);
        map.put("task_code",taskNum.getText().toString());
        map.put("task_name",task_name.getText().toString());
        map.put("task_type_code",taskTypeNum);
        map.put("task_type2",task_type2.getText().toString());
        map.put("task_info",task_info_information.getText().toString());    
        map.put("site_sno",taskSno);
        map.put("task_state",type);
        map.put("emp_ids",user_id);
        
        map.put("longitude",longitudes);
        map.put("address",tv_place.getText().toString());
        map.put("latitude",latitudes);
        map.put("address_remark",auto_operation_remark.getText().toString());
        map.put("complete_desc",edit_msg.getText().toString());
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.REPORT_TASK_URL, saveSupport, Utils.getToken(mAppContext));
        
    }
	private Handler saveSupport = new Handler() {
		public void handleMessage(android.os.Message msg) {
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					if ("01".equals(success)) {
						finish();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
                Toast.makeText(mAppContext, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};
	//任务类型
		public void taskTypes(){
			if("新建基站".equals(taskType.getText().toString())){
				taskTypeNum="T1";
			}else if("存量改造".equals(taskType.getText().toString())){
				taskTypeNum="T2";
			}else if("项目管理".equals(taskType.getText().toString())){
				taskTypeNum="T3";
			}else if("其他".equals(taskType.getText().toString())){
				taskTypeNum="T4";
			}
		}
    public void showTaskTypeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(taskTypes, whichTaskType,
				new android.content.DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichTaskType = which;
						dialog.dismiss();
						taskType.setText(taskTypes[whichTaskType]);
					}
				});
		builder.create().show();
	}
    public void showTaskPersonDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setSingleChoiceItems(taskRecievePersons,
				whichTaskRecievePerson, new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						whichTaskRecievePerson = which;
						dialog.dismiss();
						taskPerson.setText(taskRecievePersons[whichTaskRecievePerson]);
						user_id=taskRecievePersonValues[whichTaskRecievePerson];
					}
				});
		builder.create().show();
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
			newPath = ImageUtils.imageProcess(DraftReportTaskDetail2Activity.this, oldPath, 2);
			info[0] = newPath;
			mDataList.add(ImageUtils.watermarkBitmap(DraftReportTaskDetail2Activity.this, info));
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

	public void setImageAction() {
		new AlertDialog.Builder(this).setTitle(getString(R.string.app_select_photo))
				.setNegativeButton(getString(R.string.app_photo_album), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// selectImage();
						selectPicFromLocal();
					}
				}).setPositiveButton(getString(R.string.app_take_photo), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						takePhoto();
					}
				}).show();
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
				// startPhotoLocation();
				String lon = null;
				String lat = null;
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
            case 17 :
                taskSno = data.getStringExtra("taskSno");
                siteInfoName.setText(data.getStringExtra("site_name"));
                siteInfoLocation.setText("经度:"+data.getStringExtra("longitude")+"  纬度:"+data.getStringExtra("latitude"));
                siteInfoAddr.setText(data.getStringExtra("province")+"省、"+data.getStringExtra("city")+"市、"+data.getStringExtra("district"));
                Log.e(TAG, "taskSno: " + taskSno);
                break;
			default:
				break;
			}
		}
	}
}
