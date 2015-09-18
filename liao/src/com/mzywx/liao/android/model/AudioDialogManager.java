package com.mzywx.liao.android.model;

import com.mzywx.liao.android.R;
import com.mzywx.liao.android.utils.RecoderDialog;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

public class AudioDialogManager {

    private Context mContext;

    private RecoderDialog dialog;
    
    private Vibrator vibrator;
    private long[] internal = new long[]{100,10};

    public AudioDialogManager(Context context) {
        this.mContext = context;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    // 显示录音的对话框
    public void showRecordingDialog() {
    	vibrator.vibrate(internal, -1);
    	dialog = new RecoderDialog(mContext);
    	dialog.show();
    }

    public void recording() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
        	dialog.setIconVisibility(View.VISIBLE);
        	dialog.setVoiceVisibility(View.VISIBLE);
        	dialog.setLabelVisibility(View.VISIBLE);

        	dialog.setIconImageResource(R.drawable.ic_chat_recoder);
        	dialog.setLabelText(R.string.voice_recording);
        	dialog.setLabelTextBackground(R.drawable.back_record_label_normal_shape);
        }
    }

    // 显示想取消的对话框
    public void wantToCancel() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
        	dialog.setIconVisibility(View.VISIBLE);
        	dialog.setVoiceVisibility(View.GONE);
        	dialog.setLabelVisibility(View.VISIBLE);

        	dialog.setIconImageResource(R.drawable.ic_chat_cancel);
        	dialog.setLabelText(R.string.voice_record_cancel);
        	dialog.setLabelTextBackground(R.drawable.back_record_label_cancel_shape);
        }
    }

    // 显示时间过短的对话框
    public void tooShort() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
        	dialog.setIconVisibility(View.VISIBLE);
        	dialog.setVoiceVisibility(View.GONE);
        	dialog.setLabelVisibility(View.VISIBLE);

        	dialog.setIconImageResource(R.drawable.ic_chat_warning);
        	dialog.setLabelText(R.string.voice_record_too_short);
        }
    }

    // 显示取消的对话框
    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) { // 显示状态
            dialog.dismiss();
            dialog = null;
        }
    }

    // 显示更新音量级别的对话框
    public void updateVoiceLevel(int level) {
        if (dialog != null && dialog.isShowing()) { // 显示状态
        	Log.d("mikes", " voice changed level="+level);

            // 设置图片的id
            int resId = mContext.getResources().getIdentifier("ic_volume_v" + level,
                    "drawable", mContext.getPackageName());
            dialog.setVoiceImageResource(resId);
        }
    }
    
    public void updateProgress(int value){
        if (dialog != null && dialog.isShowing()) { // 显示状态
            dialog.setProgress(value);
        }
    }

}
