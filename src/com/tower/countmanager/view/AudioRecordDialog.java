package com.tower.countmanager.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tower.countmanager.R;


public class AudioRecordDialog {
    private Dialog dialog;
    private ImageView imageRecord, imageVolume;
    private TextView textHint;

    private Context context;

    public AudioRecordDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {

        dialog = new Dialog(context, R.style.Theme_RecorderDialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.chat_recorder_dialog, null);
        dialog.setContentView(view);

        imageRecord = (ImageView) dialog.findViewById(R.id.imageRecord);
        imageVolume = (ImageView) dialog.findViewById(R.id.imageVolume);
        textHint = (TextView) dialog.findViewById(R.id.textHint);

        dialog.show();
    }

    public void stateRecording() {
        if (dialog != null && dialog.isShowing()) {
            imageRecord.setVisibility(View.VISIBLE);
            imageVolume.setVisibility(View.VISIBLE);
            textHint.setVisibility(View.VISIBLE);

            imageRecord.setImageResource(R.drawable.chat_icon_dialog_recording);
            textHint.setText(R.string.voice_dialog_recording);
        }
    }

    public void stateWantCancel() {
        if (dialog != null && dialog.isShowing()) {
            imageRecord.setVisibility(View.VISIBLE);
            imageRecord.setImageResource(R.drawable.chat_icon_dialog_cancel);
            imageVolume.setVisibility(View.GONE);
            textHint.setVisibility(View.VISIBLE);
            textHint.setText(R.string.voice_dialog_cancel);
        }
    }

    public void stateLengthShort() {
        if (dialog != null && dialog.isShowing()) {
            imageRecord.setVisibility(View.VISIBLE);
            imageRecord.setImageResource(R.drawable.chat_icon_dialog_length_short);
            imageVolume.setVisibility(View.GONE);
            textHint.setVisibility(View.VISIBLE);
            textHint.setText(R.string.voice_dialog_short);
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 更新音量
     *
     * @param level
     */
    public void updateVolumeLevel(int level) {
        if (dialog != null && dialog.isShowing()) {
            // imageRecord.setVisibility(View.VISIBLE);
            // imageVolume.setVisibility(View.VISIBLE);
            // textHint.setVisibility(View.VISIBLE);

            int volumeResId = context.getResources().getIdentifier(
                    "icon_volume_" + level, "drawable",
                    context.getPackageName());
            imageVolume.setImageResource(volumeResId);
        }
    }

}
