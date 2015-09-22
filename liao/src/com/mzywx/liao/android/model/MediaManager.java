package com.mzywx.liao.android.model;

import java.io.IOException;

import com.mzywx.liao.android.AppContext;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class MediaManager {

	public interface GetDurationCallBack {
		void getDurationCallback(int duration);
	}

	private static MediaPlayer mediaPlayer;
	private static boolean isPause;

	public static void playSound(String filePath,
			OnCompletionListener onCompletionListener) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mediaPlayer.reset();
					return false;
				}
			});
		} else {
			mediaPlayer.stop();
			mediaPlayer.reset();
		}
		if (AppContext.isSpeakerOn){
		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// speaker
		} else {
		    mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);//headset
		}
		
		mediaPlayer.setOnCompletionListener(onCompletionListener);
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				Log.d("mikes", "prepared done, auto start");
				mediaPlayer.start();// when prepared done, start automatically
			}
		});
		Log.d("mikes", "play sound:path=" + filePath);
		mediaPlayer.setDataSource(filePath);
		mediaPlayer.prepareAsync();
	}
	
	public static void stopSound() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.reset();
		}
	}

	public static void getMediaDuration(String voice,
			final GetDurationCallBack callBack) {// 获取在线语音时长
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mediaPlayer.reset();
					return false;
				}
			});
		} else {
			mediaPlayer.reset();
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(voice);
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			Log.e("mikes", "e:", e);
			e.printStackTrace();
		} catch (SecurityException e) {
			Log.e("mikes", "e:", e);
			e.printStackTrace();
		} catch (IllegalStateException e) {
			Log.e("mikes", "e:", e);
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("mikes", "e:", e);
			e.printStackTrace();
		}
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer arg0) {
				callBack.getDurationCallback(mediaPlayer.getDuration() / 1000);
			}
		});
	}

	public static void pause() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			isPause = true;
		}
	}

	public static void resume() {
		if (mediaPlayer != null && isPause) {
			mediaPlayer.start();
			isPause = false;
		}
	}

	public static void release() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}
}
