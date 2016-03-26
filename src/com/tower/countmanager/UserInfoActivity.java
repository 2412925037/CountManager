package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.ImageItem;
import com.tower.countmanager.bean.UserInfoBean;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.FileUtil;
import com.tower.countmanager.util.ImageUtils;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.SelectPicPopupWindow;
import com.tower.countmanager.view.XCRoundImageView;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends FragmentActivity {

	private static final String TAG = "UserInfoActivity";

	@ViewInject(R.id.subview_title)
	TextView title;
    @ViewInject(R.id.user_icon_view)
    XCRoundImageView iconView;
    @ViewInject(R.id.user_name_view)
    TextView nameView;
    @ViewInject(R.id.user_org_view)
    TextView orgView;
    @ViewInject(R.id.user_account_view)
    TextView accountView;
    @ViewInject(R.id.user_email_view)
    TextView emailView;
    @ViewInject(R.id.user_phone_view)
    TextView phoneView;
    @ViewInject(R.id.user_sex_view)
    TextView sexView;
    @ViewInject(R.id.user_address_view)
    TextView addressView;
    @ViewInject(R.id.user_department_view)
    TextView departmentView;
    @ViewInject(R.id.user_province_view)
    TextView provinceView;
    
    //自定义的弹出框类
    SelectPicPopupWindow menuWindow = null;
    private String path = "";
    private String newPath = "";
    private ProgressDialog dialog = null;
    String empId = "";//人员编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText(R.string.user_info_title);

        initView();
    }
    
    @OnClick(value = {R.id.subview_title_arrow, R.id.user_icon_layout})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
            case R.id.user_icon_layout:
                menuWindow = new SelectPicPopupWindow(UserInfoActivity.this, itemsOnClick);
                //显示窗口
                //设置layout在PopupWindow中显示的位置
                menuWindow.showAtLocation(findViewById(R.id.user_layout),
                        Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 1);
                    break;
                default:
                    break;
            }
        }
    };

    //拍照
    public void takePhoto()
    {

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File cache = FileUtil.getUserIconDir(this);
        String newFileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File vFile = new File(cache, "/" + newFileName);

        if (!vFile.exists())
        {
            File vDirPath = vFile.getParentFile();
            vDirPath.mkdirs();

        }
        else
        {
            if (vFile.exists())
            {
                vFile.delete();
            }
        }
        path = vFile.getPath();

        Uri cameraUri = Uri.fromFile(vFile);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        startActivityForResult(openCameraIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //返回成功，将选择的图片添加到头像栏
        if(resultCode == RESULT_OK){
            if (requestCode == 0) {
                if (!TextUtils.isEmpty(path)) {

                    newPath = ImageUtils.imageProcess(this, path, 1);
                    Bitmap rawBitmap0 = BitmapFactory.decodeFile(newPath);
                    iconView.setImageBitmap(rawBitmap0);
                    commitPhoto();
                }

            }else if(requestCode == 1){
                Uri originalUri = data.getData();        //获得图片的uri
                path = ImageUtils.getImageUrl(this, originalUri);
                if (!TextUtils.isEmpty(path)) {
                    newPath = ImageUtils.imageProcess(this, path, 1);
                    Bitmap rawBitmap0 = BitmapFactory.decodeFile(newPath);
                    SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(rawBitmap0);
                    iconView.setImageBitmap(bitmapcache.get());
                    commitPhoto();
                }
            }
        }
    }

    private void createDialog() {
        if(dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

    private void initView() {
        createDialog();
        Map<String, Object> map = new HashMap<String, Object>();
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.USER_INFO_URL, handler, Utils.getToken(this));
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if(dialog != null)
                dialog.dismiss();
            Log.e(TAG, msg.obj.toString());
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String success = jsonObject.getString("success");
                        if("00".equals(success)) {
                            Toast.makeText(UserInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();
                        } else {
                            Gson gson = new Gson();
                            UserInfoBean bean = gson.fromJson(jsonObject.getString("result"), UserInfoBean.class);
                            empId = bean.getEmpId();
                            nameView.setText(bean.getEmpName());
                            orgView.setText(bean.getDutyName());
                            accountView.setText(bean.getEmpAccount());
                            emailView.setText(bean.getEmail());
                            phoneView.setText(bean.getPhone());
                            sexView.setText(bean.getSex());
                            provinceView.setText(bean.getProvinceName());
                            addressView.setText(bean.getCityName());
                            departmentView.setText(bean.getDistrictName());

                            String path = Utils.getUserPhoto(UserInfoActivity.this);
                            File tmp = new File(path);
                            if(tmp.exists()) {
                                Bitmap rawBitmap0 = BitmapFactory.decodeFile(path);
                                SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(rawBitmap0);
                                iconView.setImageBitmap(bitmapcache.get());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(UserInfoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void commitPhoto() {
        createDialog();
        List<ImageItem> mDataList = new ArrayList<ImageItem>();
        ImageItem image = new ImageItem();
        image.setDoc_path(newPath);
        mDataList.add(image);
        List<String> data = new ArrayList<String>();
        data.add(empId);
        data.add("P003");
        data.add("PERIMG");
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(data, Const.UPLOAD_PHOTO_URL, mDataList, photoHandler, Utils.getToken(this));
    }

    private Handler photoHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            if(dialog != null)
                dialog.dismiss();
            Log.e(TAG, msg.obj.toString());
            switch (msg.what) {
                case Const.REQUEST_SERVER_SUCCESS:
                    try {
                        JSONObject jsonObject = new JSONObject(msg.obj.toString());
                        String success = jsonObject.getString("success");
                        if("02".equals(success)) {
                            Utils.sessionTimeout(UserInfoActivity.this);
                        } else if("01".equals(success)){
                            Toast.makeText(UserInfoActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT).show();

                            getSharedPreferences(Const.SHARED_PREF, Activity.MODE_PRIVATE).edit()
                            .putString(Const.USER_PHOTO, newPath)
                            .commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(UserInfoActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}
