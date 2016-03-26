package com.tower.countmanager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tower.countmanager.bean.ChatMsgBean;
import com.tower.countmanager.chat.MediaManager;
import com.tower.countmanager.cnst.Const;
import com.tower.countmanager.util.AsyncImageLoader;
import com.tower.countmanager.util.RequestServerUtils;
import com.tower.countmanager.util.Utils;
import com.tower.countmanager.view.AudioRecordButton;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoiceActivity extends Activity {

	private static final String TAG = "VoiceActivity";

    @ViewInject(R.id.subview_title)
    private TextView title;
	@ViewInject(R.id.voice_rcd)
	private AudioRecordButton recordView;
    @ViewInject(R.id.voice_listview)
    private ListView mListView;

    private ProgressDialog dialog = null;
    private AnimationDrawable animation;
    private List<ChatMsgBean> mDatas = null;
    ChatMsgViewAdapter mAdapter = null;
    private View voiceAnim;

    String filePath = "";//语音文件
    String length = "";//语音长度
    String task_sno = "";//流水号
    int positionAnim = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);
		ViewUtils.inject(this);
        init();
    }

    private void init() {
        title.setText(R.string.voice_title);
        task_sno = getIntent().getStringExtra("task_sno");
        recordView.setAudioRecordFinishListener(new AudioRecordButton.AudioRecordFinishListener() {

            public void onFinish(float seconds, String filePath) {
                send(seconds, filePath);
            }
        });
        mDatas = new ArrayList<ChatMsgBean>();
        mAdapter = new ChatMsgViewAdapter(VoiceActivity.this);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

//                animation.stop();
            }
        });
        initData();
    }

    @OnClick(value = {R.id.subview_title_arrow})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.subview_title_arrow:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }

    private void send(float seconds, String filePath) {
        ChatMsgBean entity = new ChatMsgBean();
        entity.setUploadDate(Utils.getTime2String());
        entity.setEmpName(Utils.getUserId(this));
        entity.setSide("RIGHT");
        entity.setSecond_length(Math.round(seconds));
        entity.setDocPath(filePath);
        entity.setUser_img(Utils.getUserPhoto(this));

        mDatas.add(entity);
        mAdapter.notifyDataSetChanged();

        mListView.setSelection(mListView.getCount() - 1);
        this.length = Math.round(seconds) + "";
        this.filePath = filePath;
        sendMsg();
    }

    private void createDialog() {
        if (dialog == null) {
            dialog = ProgressDialog.show(this, null, getString(R.string.app_connecting_dialog_text));
            dialog.setCancelable(false);
        } else
            dialog.show();
    }

    private void sendMsg() {
        new Thread() {

            @Override
            public void run() {
                RequestServerUtils util = new RequestServerUtils();
                util.load2Server(task_sno, length, Const.UPLOAD_VOICE_FILE_URL, filePath, photoHandler, Utils.getToken(VoiceActivity.this));
            }
        }.start();

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
                            Utils.sessionTimeout(VoiceActivity.this);
                        } else if("01".equals(success))
                            Log.e(TAG, "voice upload success");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    Toast.makeText(VoiceActivity.this, R.string.app_connnect_failure, Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

    private void initData() {
        createDialog();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("task_sno", task_sno);
        RequestServerUtils util = new RequestServerUtils();
        util.load2Server(map, Const.GET_VOICE_FILE_URL, handler, Utils.getToken(this));
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
                            Toast.makeText(VoiceActivity.this, jsonObject.getString("resultdesc"), Toast.LENGTH_SHORT)
                                    .show();
                        } else if("01".equals(success)){

                            JSONObject resultObj = new JSONObject(jsonObject.getString("result"));
                            if(resultObj.getInt("total") > 0) {

                                Gson gson = new Gson();
                                Type listType = new TypeToken<ArrayList<ChatMsgBean>>(){}.getType();
                                mDatas = gson.fromJson(resultObj.getString("rows"), listType);
                            }
                            mAdapter.notifyDataSetChanged();
                            mListView.setSelection(mListView.getCount() - 1);
                        } else
                            Utils.sessionTimeout(VoiceActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case Const.REQUEST_SERVER_FAILURE:
                    break;
            }
        }
    };

    class ChatMsgViewAdapter extends BaseAdapter {

        private int minItemWidth;
        private int maxItemWidth;
        private int screenWidth;
//        private View voiceAnim;

        public ChatMsgViewAdapter(Context context) {
            screenWidth = Utils.getScreenWidth(context);
            minItemWidth = (int)(screenWidth * 0.2);
            maxItemWidth = (int)(screenWidth * 0.8);
        }

        public int getCount() {
            return mDatas.size();
        }

        public Object getItem(int position) {
            return mDatas.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getItemViewType(int position) {
            // TODO Auto-generated method stub
            ChatMsgBean entity = mDatas.get(position);

            if ("RIGHT".equalsIgnoreCase(entity.getSide())) {
                return IMsgViewType.IMVT_TO_MSG;
            } else {
                return IMsgViewType.IMVT_COM_MSG;
            }

        }

        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ChatMsgBean entity = mDatas.get(position);
            String msgType = entity.getSide();

            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                if ("LEFT".equalsIgnoreCase(msgType)) {
                    convertView = getLayoutInflater().inflate(R.layout.layout_voice_left, null);
                    viewHolder.voiceTime = (TextView) convertView.findViewById(R.id.voice_left_voice_time);
                    viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.voice_left_user_icon);
                    viewHolder.voiceLayout = (FrameLayout) convertView.findViewById(R.id.voice_left_voice_layout);
//                    viewHolder.voiceAnim = convertView.findViewById(R.id.voice_left_voice_anim);
                    viewHolder.voiceLength = (TextView) convertView.findViewById(R.id.voice_left_voice_length);
                } else {
                    convertView = getLayoutInflater().inflate(R.layout.layout_voice_right, null);
                    viewHolder.voiceTime = (TextView) convertView.findViewById(R.id.voice_right_voice_time);
                    viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.voice_right_user_icon);
                    viewHolder.voiceLayout = (FrameLayout) convertView.findViewById(R.id.voice_right_voice_layout);
//                    viewHolder.voiceAnim = convertView.findViewById(R.id.voice_right_voice_anim);
                    viewHolder.voiceLength = (TextView) convertView.findViewById(R.id.voice_right_voice_length);
                }
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.voiceTime.setText(entity.getUploadDate());
            if(!TextUtils.isEmpty(entity.getUser_img())) {
                if("LEFT".equalsIgnoreCase(msgType)) {

                    AsyncImageLoader loader = new AsyncImageLoader(viewHolder.userIcon);
                    loader.execute(entity.getUser_img());
                } else {
                    String path = Utils.getUserPhoto(VoiceActivity.this);
                    File tmp = new File(path);
                    if(tmp.exists()) {
                        Bitmap rawBitmap0 = BitmapFactory.decodeFile(path);
                        SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(rawBitmap0);
                        viewHolder.userIcon.setImageBitmap(bitmapcache.get());
                    } else
                        viewHolder.userIcon.setImageResource(R.drawable.left_menu_user_bg);
                }
            } else {

                viewHolder.userIcon.setImageResource(R.drawable.left_menu_user_bg);
            }

            int len = entity.getSecond_length();
            viewHolder.voiceLength.setText(len + "\"");
            ViewGroup.LayoutParams params = viewHolder.voiceLayout.getLayoutParams();
            if(len < 20)
                params.width = (int)(minItemWidth + maxItemWidth / 60f * len);
            else
                params.width = (int)(screenWidth * 0.6);
            viewHolder.voiceLayout.setLayoutParams(params);
            viewHolder.voiceLayout.setTag(position);

            if("LEFT".equalsIgnoreCase(msgType)) {

                viewHolder.voiceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (animation != null) {
//                            voiceAnim.setBackgroundResource(R.drawable.icon_voice_left_3);
//                            voiceAnim = null;
//                        }
                        voiceAnim = view.findViewById(R.id.voice_left_voice_anim);
                        voiceAnim.setBackgroundResource(R.drawable.anim_play_audio_left);
                        animation = (AnimationDrawable) voiceAnim.getBackground();
                        animation.start();
                        positionAnim = ((Integer)view.getTag()).intValue();
                        // 播放音频
                        MediaManager.playSound(VoiceActivity.this, mDatas.get(positionAnim).getDocPath(),
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        voiceAnim.setBackgroundResource(R.drawable.icon_voice_left_3);

                                    }
                                });

                    }
                });
            } else {

                viewHolder.voiceLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        if (animation != null) {
//                            voiceAnim.setBackgroundResource(R.drawable.icon_voice_right_3);
//                            voiceAnim = null;
//                        }
                        voiceAnim = view.findViewById(R.id.voice_right_voice_anim);
                        voiceAnim.setBackgroundResource(R.drawable.anim_play_audio_right);
                        animation = (AnimationDrawable) voiceAnim.getBackground();
                        animation.start();
                        positionAnim = ((Integer)view.getTag()).intValue();
                        // 播放音频
                        MediaManager.playSound(VoiceActivity.this, mDatas.get(positionAnim).getDocPath(),
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        voiceAnim.setBackgroundResource(R.drawable.icon_voice_right_3);
                                    }
                                });
                    }
                });
            }

            return convertView;
        }

        class ViewHolder {
            public TextView voiceTime;
            public ImageView userIcon;
            public FrameLayout voiceLayout;
//            public View voiceAnim;
            public TextView voiceLength;
        }
    }

    public interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }
}
	
