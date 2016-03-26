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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇报任务
 */

public class ReportTaskActivity extends Activity {

    private static final String TAG = "ReportTaskActivity";
    private Context mAppContext = null;
	@ViewInject(R.id.subview_title)
	private TextView subviewTitle;
	@ViewInject(R.id.subview_title_arrow)
	ImageView titleLeftImg;

	@ViewInject(R.id.custom_process_info_gridview)
	SGridView mGridView;
	@ViewInject(R.id.btn_save)
	private Button btnSave;

    //自定义的弹出框类
    SelectPicPopupWindow menuWindow = null;
    private ProgressDialog dialog = null;
    LocationUtil locationUtil = null;
    String lon = "";
    String lat = "";
    BDLocation bdLocation = null;
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
	@ViewInject(R.id.task_name_title)
	TextView task_name_title;
	@ViewInject(R.id.task_info_person_title)
	TextView task_info_person_title;
	
	@ViewInject(R.id.sale_chance_info_chance_name_title)
	TextView sale_chance_info_chance_name_title;
	@ViewInject(R.id.task_num_title)
	TextView task_num_title;
	@ViewInject(R.id.tv_pic)
	TextView tv_pic;
	@ViewInject(R.id.tv_location)
	TextView tv_location;
	@ViewInject(R.id.bar2)
	ProgressBar bar;
	private String[] taskTypes;
	private String[] taskRecievePersons;
	private String[] taskRecievePersonValues;
	private String[] emergencyDegree;
	private String[] multipleFeedback;
	@ViewInject(R.id.layout_recieve_person)
	RelativeLayout layout_recieve_person;
	int whichTaskType = 0;
	int whichTaskRecievePerson = 0;
	private String type="";
	private String taskTypeNum="";//任务类型编码
	private String user_id="";
	String longitudes;
	String latitudes;
	private String task_sno;
	private String task_names;
	private String user_name="";
	boolean[] selected = {false,false,false,false};  
	List<String> name=new ArrayList<String>();
	List<String> id=new ArrayList<String>();
	// 读系统照片相关字段信息
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN,
			MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media._ID };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_task);
		ViewUtils.inject(this);
		 mAppContext = this.getApplicationContext();
		initViews();
		initGridView();
	}
	
	
	private void initViews(){
		layout_recieve_person.setVisibility(View.GONE);
		emergencyDegree = getResources().getStringArray(R.array.task_emergency_degree);
		multipleFeedback = getResources().getStringArray(R.array.task_multiple_feedback);
		
		sale_chance_info_chance_name_title.setText(Html.fromHtml("任务类型"+"<font color=\"#db4537\"> *</font>"));
		task_num_title.setText(Html.fromHtml("任务编号"+"<font color=\"#db4537\"> *</font>"));
		task_name_title.setText(Html.fromHtml("任务名称"+"<font color=\"#db4537\"> *</font>"));
		tv_pic.setText(Html.fromHtml("上传照片"+"<font color=\"#db4537\"> *</font>"));
		tv_location.setText(Html.fromHtml("定位"+"<font color=\"#db4537\"> *</font>"));
		
		subviewTitle.setText(R.string.report_task_title);
		titleLeftImg.setVisibility(View.VISIBLE);
		taskTypes = getResources().getStringArray(R.array.task_type);
		loadTaskRecievePerson();
		loadTaskInfo();
        locationUtil = new LocationUtil(this);
        locationUtil.getLocation(new LocationUtil.LocationCallBack() {
		@Override
		public void onSuccess(BDLocation location) {
			tv_place.setText(location.getAddrStr());
			latitudes=location.getLatitude()+"";
			longitudes=location.getLongitude()+"";
			tv_while.setText(String.format(getString(R.string.auto_operation_location_longitude),
					longitudes)+","
					+ String.format(getString(R.string.auto_operation_location_latitude),
							latitudes));
            bdLocation = location;
            lon = location.getLongitude() + "";
            lat = location.getLatitude() + "";
		}
		});
	}

	private void initGridView() {
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

		mAdapter = new ImagePublishAdapter(this, mDataList, true, Const.MAX_IMAGE_SIZE);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == getDataSize(mDataList)) {
                    menuWindow = new SelectPicPopupWindow(ReportTaskActivity.this, itemsOnClick);
                    //显示窗口
                    //设置layout在PopupWindow中显示的位置
                    menuWindow.showAtLocation(findViewById(R.id.report_task_layout),
                            Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
				} else {
					List<String> pathList = new ArrayList<String>();
					for (ImageItem imageItem : mDataList) {
						pathList.add(imageItem.getDoc_path());
					}
					Intent intent = new Intent(ReportTaskActivity.this, ImageZoomActivity.class);
					intent.putStringArrayListExtra(Const.EXTRA_IMAGE_LIST, (ArrayList<String>) pathList);
					intent.putExtra(Const.EXTRA_CURRENT_IMG_POSITION, position);
					intent.putExtra(Const.IS_SHOW_DEL_BTN, true);
					startActivityForResult(intent, REQUEST_ACTIVITY_IMAGE_ZOOM);
				}
			}
		});
		
	}

    @OnClick(value = {R.id.subview_title_arrow, R.id.voice_message,R.id.task_type, R.id.report_task_site_info_layout
    		,R.id.btn_save,R.id.btn_commit,R.id.layout_recieve_person,R.id.layout_refresh})
    public void onButtonClick(View v){
    	task_names=task_name.getText().toString();
        switch(v.getId()) {
            case R.id.subview_title_arrow :
                finish();
                break;
            case R.id.voice_message :
                startActivity(new Intent(this, VoiceActivity.class).putExtra("task_sno", task_sno));
                break;
            case R.id.task_type:
    			showTaskTypeDialog();
    			break;
            case R.id.report_task_site_info_layout :
    			startActivityForResult(new Intent(this, CreateSiteInfokActivity.class).putExtra("site_sno", taskSno), REQUEST_ACTIVITY_REMARK_INFO);
                break;
            case R.id.btn_save:
    			type="1";
    		      taskTypes();
    		      commitPhoto();
//  				temporarySave(type);
    			break;
    		case R.id.btn_commit:
    			type="2";
    		      taskTypes();
    		      commitPhoto();
    			
    			break;
    		case R.id.layout_recieve_person://任务接收人
    			if (taskRecievePersons.length > 0) {
    				System.out.println(taskRecievePersons.length);
    				showTaskPersonDialog();
    			}
    			break;
    		case R.id.layout_refresh:
    			getLacationMsg();
    			break;
        }
    }

    /**
     * 
     */
    public void getLacationMsg(){
    	tv_place.setText("");
    	tv_while.setText("");
    	bar.setVisibility(View.VISIBLE);
    	LocationUtil u = new LocationUtil(this);
		u.getLocation(new LocationUtil.LocationCallBack() {
		@Override
		public void onSuccess(BDLocation location) {
			tv_place.setText(location.getAddrStr());
			latitudes=location.getLatitude()+"";
			longitudes=location.getLongitude()+"";
			tv_while.setText(String.format(getString(R.string.auto_operation_location_longitude),
					longitudes)+","
					+ String.format(getString(R.string.auto_operation_location_latitude),
							latitudes));
			bar.setVisibility(View.GONE);
		}
		});
		
    }
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
						Toast.makeText(ReportTaskActivity.this, jsonObject.getString("resultdesc"),
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
				Toast.makeText(ReportTaskActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	/**
	 * 拉取任务信息
	 */
	private void loadTaskInfo() {
		RequestServerUtils util = new RequestServerUtils();
		util.load2Server(null, Const.GET_TASKN_CODE_URL, handlerLoad, Utils.getToken(this));
	}
	private Handler handlerLoad = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					if ("00".equals(success)) {
						Toast.makeText(ReportTaskActivity.this, jsonObject.getString("resultdesc"),
								Toast.LENGTH_SHORT).show();
					} else {
						JSONObject jsonObject2 = jsonObject.getJSONObject("result");
						if (jsonObject2 != null) {
							String task_code = jsonObject2.getString("task_code");
							taskNum.setText(task_code);
							//TODO 
							task_sno=jsonObject2.getString("task_sno");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case Const.REQUEST_SERVER_FAILURE:
				Toast.makeText(ReportTaskActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

    private void createDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

    private void commitPhoto() {
        createDialog();
        List<String> data = new ArrayList<String>();
        data.add(task_sno);
        data.add("P001");
        data.add("GZHB");
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
                            Utils.sessionTimeout(ReportTaskActivity.this);
                            if (dialog != null)
                                dialog.dismiss();
                        } else if("01".equals(success)){
                        	temporarySave(type);
                        } else if("00".equals(success)) {
                            Toast.makeText(ReportTaskActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(ReportTaskActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    if (dialog != null)
                        dialog.dismiss();
                    break;
            }
        }
    };

    private void temporarySave(String type) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("task_sno",task_sno);
        map.put("task_code",taskNum.getText().toString());
        map.put("task_name",task_names);
        map.put("task_type_code",taskTypeNum);
        map.put("task_type2",task_type2.getText().toString());
        map.put("task_info",task_info_information.getText().toString());    
        map.put("site_sno",taskSno);
        map.put("task_state",type);
//        map.put("emp_ids",user_id);
        
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
			if (dialog != null)
                dialog.dismiss();
			Log.e(TAG, (String) msg.obj);
			switch (msg.what) {
			case Const.REQUEST_SERVER_SUCCESS:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String success = jsonObject.getString("success");
					Toast.makeText(mAppContext, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
					if ("01".equals(success)) {
                        finish();
					} else if("00".equals(success)) {
                        Toast.makeText(ReportTaskActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setMultiChoiceItems(taskRecievePersons, selected,
				new DialogInterface.OnMultiChoiceClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which, boolean isChecked) {
						// TODO Auto-generated method stub
						whichTaskRecievePerson = which;
						selected[whichTaskRecievePerson] = isChecked;  
						dialog.dismiss();
						if(selected[whichTaskRecievePerson]==true){
							name.add(taskRecievePersons[whichTaskRecievePerson]);
							id.add(taskRecievePersonValues[whichTaskRecievePerson]);
						   }else{
							   name.remove(taskRecievePersons[whichTaskRecievePerson]);
							   id.remove(taskRecievePersonValues[whichTaskRecievePerson]);  
						   }
						
						for(int i=0;i<name.size();i++){
							if(i==0){
								user_name=name.get(0);
								user_id=id.get(0);
							}else{
								user_name=user_name+","+name.get(i);
								user_id=user_id+","+id.get(i);
							}
						}
							taskPerson.setText(user_name);
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
			newPath = ImageUtils.imageProcess(ReportTaskActivity.this, oldPath, 2);
			info[0] = newPath;
			mDataList.add(ImageUtils.watermarkBitmap(ReportTaskActivity.this, info));
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
            case 17 :
                taskSno = data.getStringExtra("taskSno");
                siteInfoName.setText(data.getStringExtra("site_name"));
                if(TextUtils.isEmpty(data.getStringExtra("longitude"))&&TextUtils.isEmpty(data.getStringExtra("latitude"))){
	                siteInfoLocation.setText("经度:未知  纬度:未知");
                }else if(TextUtils.isEmpty(data.getStringExtra("longitude"))){
	                siteInfoLocation.setText("纬度:"+data.getStringExtra("latitude"));
                }else if(TextUtils.isEmpty(data.getStringExtra("latitude"))){
	                siteInfoLocation.setText("经度:"+data.getStringExtra("longitude"));
                }else{
	                siteInfoLocation.setText("经度:"+data.getStringExtra("longitude")+"  纬度:"+data.getStringExtra("latitude"));
                }                  
               // siteInfoAddr.setText(data.getStringExtra("province")+"、"+data.getStringExtra("city")+"、"+data.getStringExtra("district"));
                Log.e(TAG, "taskSno: " + taskSno);
                break;
			default:
				break;
			}
		}
	}
}
