package com.mzywx.liao.android;

import org.litepal.LitePalApplication;

import cn.jpush.android.api.JPushInterface;
import android.util.Log;

public class AppContext extends LitePalApplication {
	private static final String TAG = "JPush";
	public static final String VOICE_PATH = "/storage/sdcard0/liao_chat/voice";
	public static final String CAMERA_PATH = "/storage/sdcard0/liao_chat/camera";

	@Override
	public void onCreate() {
		Log.d(TAG, "[AppContext] onCreate");
		super.onCreate();

		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
	}
}
