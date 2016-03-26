package com.tower.countmanager.chat;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

public class MediaManager {
	private static MediaPlayer mediaPlayer;
	private static boolean isPause;

	public static void playSound(Context context, String filePath,
			OnCompletionListener onCompletionListener) {
		if (mediaPlayer != null) {
//            if(mediaPlayer.isPlaying())
//                mediaPlayer.stop();
            mediaPlayer.release();
		}
		try {
            mediaPlayer = MediaPlayer.create(context, Uri.parse(filePath));
//            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//
//                @Override
//                public boolean onError(MediaPlayer mp, int what, int extra) {
////                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                    return false;
//                }
//            });
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
//			mediaPlayer.setDataSource(filePath);
//			mediaPlayer.prepare();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}
	
	public static void resume(){
		if (mediaPlayer != null && isPause) {
			mediaPlayer.start();
			isPause = false;
		}
	}
	
	public static void release(){
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

}
