package com.mzywx.liao.android;

import cn.jpush.android.api.JPushInterface;
import android.app.Application;
import android.util.Log;

public class AppContext extends Application{
    private static final String TAG = "JPush";

    @Override
    public void onCreate() {             
         Log.d(TAG, "[AppContext] onCreate");
         super.onCreate();
         
         JPushInterface.setDebugMode(true);     // 设置开启日志,发布时请关闭日志
         JPushInterface.init(this);             // 初始化 JPush
    }
}
